# 乞丐版的Netty

Netty是非常值得深入研究的项目，为了防止眼高手低，所以实现了一个乞丐版，用来演示自己在Netty中的一些心得。
**作者能力有限，如果有什么错误的地方，欢迎大家斧正**

## 为什么要学习Netty

## Netty基本概念

> Netty is an asynchronous event-driven network application framework
for rapid development of maintainable high performance protocol servers & clients.

从官网的描述，我们可以看出Netty是**异步事件驱动**的**网络编程框架**
这里有两个很重要的词：

1. 异步事件驱动
2. 网络编程框架

以下是我对这两个词的解读：

Netty对Java的Future和Executor机制进行了扩展，设计了自己的Future，Promise，EventExecutor和Listener，来实现异步事件驱动

Java虽然提供了ServerSocketChannel，SocketChannel，Selector，Buffer等来支持NIO模型的网络编程，但是这些更像是一个个独立的“螺丝钉”，需要程序员自己将其组合实现网络通信。

基于Java提供的类，实现一个Demo简单，实现一个生产可用且稳定的产品就非常，非常难了

Netty底层还是用的Java提供的类，并没有重复皂轮子，但是设计了Channel模型，ChannelPipeline，ChannelHandler，ServerBootstrap，Bootstrap等，将网络通信过程中的细节封装，并且ChannelPipeline和ChannelHandler的设计提供了极强大的扩展能力。

大部分业务需求，都能通过扩展ChannelPipeline和ChannelHandler来实现，另外，Netty也提供了很多Handler，比如：编解码的Handler，处理半包，沾包的Handler。

开发者开发一个网络应用，比如：RPC，web服务器，甚至自定义网络协议等，变得极其简单，让网络编程“飞入寻常百姓家”，所以说Netty是一个网络编程框架

> Netty的设计：举一个不是很严谨的对比例子：在做一些业务项目时，一些开发者会提供直接对数据库表的CRUD的RPC服务，看起来很通用，而且所有的业务必定能通过组装这些PRC服务实现，但是这种设计，对调用者暴漏了太多信息，导致调用者很难使用，也导致服务提供者后续维护的困难，一旦有变动，就可能要梳理所有的调用者是否有影响
> Java的NIO类，就可以类比是CURD服务，Netty则是封装后的领域服务，封装了通信细节，只需要使用ChannelPipeline和ChannelHandler机制实现自己的需求即可

## 源码解读

我在看完Netty的源码，做了一些自己的解读，可能会有一些片面，欢迎留言讨论

我将Netty的源码解读分成了三个模块：

1. Netty对Java的Concurrent包的扩展，包括扩展：Future,Promise,EventExecutor,EventExecutorGroup等
2. Channel模型，包括：Channel,ChannelOutbound,ChannelInbound,Unsafe,ChannelPipeline,ChannelHandler
3. Buffer，Netty的Buffer是非常重要的，Netty通过BufferPool复用，以及合理的Buffer分配策略，来提升性能
