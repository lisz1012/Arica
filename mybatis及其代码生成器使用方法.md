# MyBatis 及其代码生成器使用方法

0. pom.xml 文件加入依赖：
   ```
   <dependency>
       <groupId>org.mybatis.spring.boot</groupId>
       <artifactId>mybatis-spring-boot-starter</artifactId>
       <version>2.1.3</version>
   </dependency>
   ```
1.（有了就不用下载了）在这里有源码https://github.com/zouzg/mybatis-generator-gui 用Git把代码拉下来并且创建工具项目：https://github.com/zouzg/mybatis-generator-gui.git
2. 在这个工具项目中运行MainUI （注意，在自己写代码的机器上运行的时候，必须用JDK 8）
3. 在左上角点击配置并连接数据库
4. 选中目标表
5. 填写表和生成代码的信息，可以下生成到一个无关的目录下然后再手动拷贝过来，以免出错覆盖
6. 回到我们的工作项目，把生成的实体类和实体类的Example类放到形如com.lisz.arica.entity的package下
7. 项目启动类的脑袋上面加上类似 @MapperScan("com.lisz.arica.mapper") 的mapper扫描范围限定
8. 把生成的DAO和BaseDAO放在形如com.lisz.arica.entity的package下
9. 把XxxDAO.xml放在resources下的mapper目录下
10. 尝试启动 Springboot 可能会报错，有些路径不对，修改xml文件。以上三步，在做的时候可能需要修改点东西