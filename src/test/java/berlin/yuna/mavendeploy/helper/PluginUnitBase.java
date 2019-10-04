package berlin.yuna.mavendeploy.helper;

import berlin.yuna.mavendeploy.config.ReadmeBuilder;
import berlin.yuna.mavendeploy.logic.GitService;
import berlin.yuna.mavendeploy.model.Logger;
import berlin.yuna.mavendeploy.plugin.PluginExecutor;
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
import java.util.Properties;

import static berlin.yuna.mavendeploy.helper.CustomMavenTestFramework.DEBUG;
import static berlin.yuna.mavendeploy.helper.CustomMavenTestFramework.getPomFile;
import static berlin.yuna.mavendeploy.logic.AdditionalPropertyReader.readDeveloperProperties;
import static berlin.yuna.mavendeploy.logic.AdditionalPropertyReader.readLicenseProperties;
import static berlin.yuna.mavendeploy.logic.AdditionalPropertyReader.readModuleProperties;
import static java.lang.String.format;
import static org.codehaus.plexus.logging.Logger.LEVEL_DEBUG;
import static org.codehaus.plexus.logging.Logger.LEVEL_DISABLED;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PluginUnitBase {

    protected Logger log;
    protected PluginExecutor.ExecutionEnvironment environment;
    protected MavenProject mavenProject;
    protected String projectPath;
    protected PluginSession session;

    @Before
    public void setUp() {
        session = createTestSession();
        log = session.getLog();
        mavenProject = session.getProject();
        environment = session.getEnvironment();
        projectPath = session.getProject().getFile().getParent();
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

        ReadmeBuilder.build(session).render();

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

    public static PluginSession createTestSession() {
        final File pomFile = new File(System.getProperty("user.dir"), "pom.xml");
        final Model project = getPomFile(pomFile);
        project.setPomFile(pomFile);
        final MavenProject mavenProject = new MavenProject(project);
        mavenProject.setFile(project.getPomFile());
        final MavenSession mavenSession = mock(MavenSession.class);
        final PluginExecutor.ExecutionEnvironment environment = new PluginExecutor.ExecutionEnvironment(mavenProject, mavenSession, new DefaultBuildPluginManager());
        final PluginSession pluginSession = new PluginSession(environment);
        pluginSession.getLog().setLogLevel(DEBUG? LEVEL_DEBUG : LEVEL_DISABLED);
        final Properties properties = prepareProperties(mavenProject, pluginSession.getLog());
        when(mavenSession.getUserProperties()).thenReturn(properties);
        when(mavenSession.getSystemProperties()).thenReturn(System.getProperties());
        return pluginSession;
    }

    protected static String readFile(final File file) throws IOException {
        assertThat(format("File [%s] doesnt exists", file), file.exists(), is(true));
        return Files.readString(file.toPath());
    }

    private static Properties prepareProperties(final MavenProject project, final Logger log) {
        final Properties properties = new Properties();
        properties.putAll(new GitService(log, project.getBasedir(), true).getConfig());
        properties.putAll(project.getProperties());
        properties.put("project.basedir", project.getBasedir());
        properties.put("project.baseUri", project.getBasedir());
        properties.put("project.build.directory", new File(project.getBasedir(), "target"));
        properties.put("project.name", project.getArtifactId());
        properties.put("project.version", project.getVersion());
        properties.put("project.artifactId", project.getArtifactId());
        properties.put("project.groupId", project.getGroupId());
        properties.put("project.packaging", project.getPackaging());
        properties.put("project.description", project.getDescription().replaceAll(" +", " ").replaceAll("\n ", "\n").replace("\t", ""));
        properties.putAll(readDeveloperProperties(project.getDevelopers()));
        properties.putAll(readLicenseProperties(project.getLicenses()));
        properties.putAll(readModuleProperties(project.getModules()));
        return properties;
    }
}
