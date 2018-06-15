spring 官网jar 部署文档

http://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html



想要打包出的jar包含执行脚本，在Spring Boot Maven插件中添加如下配置：
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
	    <!-- 指定jar包run入口 -->
        <mainClass>com.zklc.vp.Application</mainClass>
        <executable>true</executable>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>repackage</goal>
            </goals>
        </execution>
    </executions>
</plugin>
按照Spring Boot正常的打包流程，打包应用，生成jar文件。

上传jar文件到服务器，直接将jar文件配置为服务执行脚本。

# 确保jar文件有执行权限
#生产环境执行命令
chmod 500 serviceA.jar --spring.profiles.active=prod
# 创建jar文件到/etc/init.d/的软连接
ln -s /www/serviceA.jar /etc/init.d/myservice
ps：创建软连接时，一定要指定jar文件的绝对路径。建立软连接后，可以查看/etc/init.d/目录下多了myservice文件，这个文件就是包含在jar中的执行脚本内容。不妨预览一下脚本内容，其中内容还是挺全面的，包括chkconfig命令的选项声明，可以方便地使用chkconfig命令将服务注册为开机启动，无需做任何修改。

使用service命令运行应用
service myservice start --spring.profiles.active=dev
