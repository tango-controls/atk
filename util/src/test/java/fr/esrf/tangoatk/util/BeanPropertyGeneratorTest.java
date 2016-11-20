package fr.esrf.tangoatk.util;

import org.apache.maven.project.MavenProject;
import org.junit.Assert;
import org.junit.Before;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author ingvord
 * @since 11/20/16
 */
public class BeanPropertyGeneratorTest {
    private BeanPropertyGenerator instance;

    @Before
    public void before(){
        instance = new BeanPropertyGenerator();

        Map<String, Object> context = new HashMap<String, Object>();

        MavenProject mockProject = mock(MavenProject.class);

        doReturn(new File("target/test-classes")).when(mockProject).getBasedir();

        context.put(BeanPropertyGenerator.PROJECT, mockProject);

        instance.setPluginContext(context);
    }

    @org.junit.Test
    public void execute() throws Exception {
        instance.execute();

        assertTrue(new File("target/test-classes/src/main/java/test/Test.java").exists());

        //TODO assert diff against expected result
    }

}