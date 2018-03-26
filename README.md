This is a product-level project using netty to communicating message.


netty原理见各种帖子
这个工程用法：
  server端
    1. 在ServerMain里注册消息处理类：com.chris.example.RLRModule。
    2. 开发1里注册的类：主要是与客户端交互的类，定义好接口，参数传递格式即可。
  client端
    1. python的话用websocketclient,见com.chris.example.client.python


