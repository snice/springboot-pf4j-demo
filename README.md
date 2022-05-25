# springboot-pf4j

pf4j for spring boot 

## 特性

- 支持Controller
- 支持jpa
- 支持Service
- 支持static映射
  <h3>/plugin/** ----- *.jar/static/**</h3>
- 支持Freemarker
- 支持thymeleaf

## 示例

- main - spring boot主项目
- pf4j-spring - spring pf4j 插件
- plugin1 - jar插件
- plugin2 - zip插件(包含lib)

## 插件特性

- 热启动
- 支持第三方lib（仅zip插件）

- 停止指定插件

  http://localhost:8080/plugins/stop?id=test-plugin2

- 启动指定插件

  http://localhost:8080/plugins/start?id=test-plugin2