# springboot-pf4j

pf4j for spring boot 

## 目标

- 热启动
- 支持第三方lib（仅zip插件）

## 插件已支持特性

- 支持Controller
- 支持jpa
- 支持Service
- 支持Interceptor
- 支持static映射

  <h3>*.jar/static/***  ----->  http://xx/_plugins/static/**  </h3>
- 支持Freemarker
- 支持thymeleaf

## 示例

- main - spring boot主项目
- pf4j-spring - spring pf4j 插件
- plugin1 - jar插件
- plugin2 - zip插件(包含lib)

## 测试

- 停止指定插件

  http://localhost:8080/plugins/stop?id=test-plugin2

- 启动指定插件

  http://localhost:8080/plugins/start?id=test-plugin2