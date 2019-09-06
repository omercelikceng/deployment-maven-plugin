package berlin.yuna.mavendeploy.config;

import berlin.yuna.mavendeploy.logic.GitService;
import berlin.yuna.mavendeploy.model.Logger;
import berlin.yuna.mavendeploy.plugin.MojoExecutor;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.DefaultBuildPluginManager;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

import static berlin.yuna.mavendeploy.CustomMavenTestFramework.getPomFile;
import static berlin.yuna.mavendeploy.plugin.MojoRun.readDeveloperProperties;
import static berlin.yuna.mavendeploy.plugin.MojoRun.readLicenseProperties;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReadmeBuilderTest {

    private static final Logger log = new Logger(null);
    private MojoExecutor.ExecutionEnvironment environment;
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
        environment = new MojoExecutor.ExecutionEnvironment(mavenProject, mavenSession, new DefaultBuildPluginManager());
    }

    @Test
    public void runBuilder() throws IOException {
        final String input = readFile(new File(projectPath, "README.builder.md"));
        assertThat(input, containsString("[var builder_usage]: # (Usage as)"));
        assertThat(input, containsString("[var builder_usage_plugin]: # (!{builder_usage} plugin)"));
        assertThat(input, containsString("[var builder_usage_command]: # (!{builder_usage} command line)"));
        assertThat(input, containsString("# !{project.name}"));
        assertThat(input, containsString("### !{builder_usage_plugin}"));
        assertThat(input, containsString("### !{builder_usage_command}"));
        assertThat(input, containsString("<artifactId>!{project.artifactId}</artifactId>"));

        ReadmeBuilder.build(environment, log).render();

        final String result = readFile(new File(projectPath, "README.md"));
        assertThat(result, containsString("# " + mavenProject.getArtifactId()));
        assertThat(result, containsString("<artifactId>" + mavenProject.getArtifactId() + "</artifactId>"));
        assertThat(result, containsString("### Usage as command line"));
        assertThat(result, containsString("### Usage as command line"));
        assertThat(result, not(containsString("[var builder_usage_command]: # (!{builder_usage} command line)")));
    }

    private String readFile(final File file) throws IOException {
        assertThat(format("File [%s] doesnt exists", file), file.exists(), is(true));
        return new String(Files.readAllBytes(file.toPath()), UTF_8);
    }

    private Properties prepareProperties(final MavenProject mavenProject) {
        final Properties properties = new GitService(log, mavenProject.getBasedir(), true).getConfig();
        properties.put("project.basedir", mavenProject.getBasedir());
        properties.put("project.baseUri", mavenProject.getBasedir());
        properties.put("project.build.directory", new File(mavenProject.getBasedir(), "target"));
        properties.put("project.name", mavenProject.getArtifactId());
        properties.put("project.version", mavenProject.getVersion());
        properties.put("project.artifactId", mavenProject.getArtifactId());
        properties.put("project.groupId", mavenProject.getGroupId());
        properties.put("project.packaging", mavenProject.getPackaging());
        properties.put("project.description", mavenProject.getDescription().replaceAll(" +", " ").replaceAll("\n ", "\n"));
        properties.putAll(readDeveloperProperties(mavenProject));
        properties.putAll(readLicenseProperties(mavenProject));
        return properties;
    }
}
