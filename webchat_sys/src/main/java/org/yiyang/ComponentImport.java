package org.yiyang;

import com.github.tobato.fastdfs.FdfsClientConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.Import;
import org.springframework.jmx.support.RegistrationPolicy;

/**
 * Import FastDFS-Client components
 */
@Configuration
@Import(FdfsClientConfig.class)
// Solve the problem that jmx repeatedly registering beans
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
public class ComponentImport {
}