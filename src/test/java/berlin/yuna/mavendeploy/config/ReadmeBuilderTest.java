package berlin.yuna.mavendeploy.config;

import berlin.yuna.mavendeploy.logic.GitService;
import berlin.yuna.mavendeploy.model.Logger;
import berlin.yuna.mavendeploy.plugin.PluginExecutor;
import berlin.yuna.mavendeploy.plugin.Application;
import berlin.yuna.mavendeploy.plugin.PluginSession;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.DefaultBuildPluginManager;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static berlin.yuna.mavendeploy.helper.CustomMavenTestFramework.DEBUG;
import static berlin.yuna.mavendeploy.helper.CustomMavenTestFramework.getPath;
import static berlin.yuna.mavendeploy.helper.CustomMavenTestFramework.getPomFile;
import static berlin.yuna.mavendeploy.logic.AdditionalPropertyReader.readDeveloperProperties;
import static berlin.yuna.mavendeploy.logic.AdditionalPropertyReader.readLicenseProperties;
import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReadmeBuilderTest {

    private static final Logger log = new Logger().enableDebug(DEBUG);
    private PluginExecutor.ExecutionEnvironment environment;
    private MavenProject mavenProject;
    private String projectPath = System.getProperty("user.dir");

    @Before
    public void setUp() {
        final File pomFile = new File(projectPath, "pom.xml");
        final Model project = getPomFile(pomFile);
        project.setPomFile(pomFile);
        mavenProject = new MavenProject(project);
        mavenProject.setFile(project.getPomFile());
        final MavenSession mavenSession = mock(MavenSession.class);
        final Properties properties = prepareProperties(mavenProject);
        when(mavenSession.getUserProperties()).thenReturn(properties);
        when(mavenSession.getSystemProperties()).thenReturn(System.getProperties());
        environment = new PluginExecutor.ExecutionEnvironment(mavenProject, mavenSession, new DefaultBuildPluginManager());
    }

    @Test
    public void runBuilder() throws IOException {
        final String input = readFile(new File(projectPath, "README/README.builder.md"));
        assertThat(input, containsString("[var builder_usage]: # (Usage as)"));
        assertThat(input, containsString("[var builder_usage_plugin]: # (!{builder_usage} plugin)"));
        assertThat(input, containsString("[var builder_usage_command]: # (!{builder_usage} command line)"));
        assertThat(input, containsString("# !{project.name}"));
        assertThat(input, containsString("### !{builder_usage_plugin}"));
        assertThat(input, containsString("### !{builder_usage_command}"));
        assertThat(input, containsString("<artifactId>!{project.artifactId}</artifactId>"));
        assertThat(input, containsString("[include]: # (/README/shields.include.md)"));
        assertThat(input, containsString("*autogenerated please use the builder file to change the [include]: # (test.include.md)*"));

        ReadmeBuilder.build(new PluginSession(environment, log)).render();

        final String result = readFile(new File(projectPath, "README.md"));
        assertThat(result, containsString("# " + mavenProject.getArtifactId()));
        assertThat(result, containsString("<artifactId>" + mavenProject.getArtifactId() + "</artifactId>"));
        assertThat(result, containsString("### Usage as plugin"));
        assertThat(result, containsString("### Usage as command line"));
        assertThat(result, containsString("![License][License-shield]"));
        assertThat(result, containsString("*autogenerated please use the builder file to change the content*"));
        assertThat(result, not(containsString("[var builder_usage_command]: # (!{builder_usage} command line)")));
        assertThat(result, not(containsString("[include]: # (/README/shields.include.md)")));
    }

    @Test
    public void setFallbackJavaVersion() throws IOException {
        final String javaVersion = environment.getMavenSession().getUserProperties().getProperty("java.version");
        final Path path = getPath(Application.class);
        final String content = Files.readString(path);
        final String result = content.replaceFirst("(?<prefix>.*JAVA_VERSION.*\")(?<version>.*)(?<suffix>\".*)", "${prefix}" + javaVersion + "${suffix}");
        Files.writeString(path, result);
    }

    private String readFile(final File file) throws IOException {
        assertThat(format("File [%s] doesnt exists", file), file.exists(), is(true));
        return Files.readString(file.toPath());
    }

    private Properties prepareProperties(final MavenProject mavenProject) {
        final Properties properties = new Properties();
        properties.putAll(new GitService(log, mavenProject.getBasedir(), true).getConfig());
        properties.putAll(mavenProject.getProperties());
        properties.put("project.basedir", mavenProject.getBasedir());
        properties.put("project.baseUri", mavenProject.getBasedir());
        properties.put("project.build.directory", new File(mavenProject.getBasedir(), "target"));
        properties.put("project.name", mavenProject.getArtifactId());
        properties.put("project.version", mavenProject.getVersion());
        properties.put("project.artifactId", mavenProject.getArtifactId());
        properties.put("project.groupId", mavenProject.getGroupId());
        properties.put("project.packaging", mavenProject.getPackaging());
        properties.put("project.description", mavenProject.getDescription().replaceAll(" +", " ").replaceAll("\n ", "\n"));
        properties.putAll(readDeveloperProperties(mavenProject.getDevelopers()));
        properties.putAll(readLicenseProperties(mavenProject.getLicenses()));
        return properties;
    }
}
