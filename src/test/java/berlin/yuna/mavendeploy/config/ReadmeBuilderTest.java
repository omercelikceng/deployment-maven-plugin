package berlin.yuna.mavendeploy.config;

import berlin.yuna.mavendeploy.helper.PluginUnitBase;
import berlin.yuna.mavendeploy.plugin.Application;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static berlin.yuna.mavendeploy.helper.CustomMavenTestFramework.getPath;
import static berlin.yuna.mavendeploy.model.Parameter.JAVA_VERSION;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;

public class ReadmeBuilderTest extends PluginUnitBase {

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

    @Test
    public void setFallbackJavaVersion() throws IOException {
        final String javaVersion = environment.getMavenSession().getUserProperties().getProperty(JAVA_VERSION.key());
        final Path path = getPath(Application.class);
        final String content = Files.readString(path);
        final String result = content.replaceFirst("(?<prefix>.*JAVA_VERSION.*\")(?<version>.*)(?<suffix>\".*)", "${prefix}" + javaVersion + "${suffix}");
        Files.writeString(path, result);
    }
}
