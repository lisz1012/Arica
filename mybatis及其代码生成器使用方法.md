# MyBatis 及其代码生成器使用方法

1. 在这里有源码https://github.com/zouzg/mybatis-generator-gui 用Git把代码拉下来并且创建项目：https://github.com/zouzg/mybatis-generator-gui.git
2. 运行MainUI （注意，在自己写代码的机器上运行的时候，必须用JDK 8）
3. 在左上角点击配置并连接数据库
4. 选中目标表
5. 填写表和生成代码的信息，可以下生成到一个无关的目录下然后再手动拷贝过来，以免出错覆盖
6. 把生成的实体类和实体类的Example类放到形如com.lisz.arica.entity的package下
7. 把生成的DAO和BaseDAO放在形如com.lisz.arica.entity的package下
8. 把XxxDAO.xml放在resources下的mapper目录下
9. 尝试启动 Springboot 可能会报错，有些路径不对，修改xml文件。以上三步，在做的时候可能需要修改点东西