---
title: Go并发模式详解
category: language
tags: [tech, go, concurrency, patterns]
created: 2024-12-15
updated: 2024-12-17
source: 实践总结 + 官方文档
---

# 🔧 Go并发模式详解

## 📖 概述
> 一句话说明是什么

Go语言的并发模式是基于CSP（Communicating Sequential Processes）理论，通过goroutine和channel实现高效的并发编程。

## 🎯 使用场景
> 什么时候用，解决什么问题

- 场景1：需要同时处理多个独立任务
- 场景2：I/O密集型操作的并发处理
- 场景3：生产者-消费者模式实现
- 场景4：并发任务的超时控制

## 💡 核心概念

### Goroutine
轻量级线程，由Go运行时管理，创建成本极低（约2KB栈空间）

### Channel
goroutine之间的通信管道，遵循"不要通过共享内存来通信，而应该通过通信来共享内存"的设计理念

### Select
多路复用，可以同时等待多个channel操作

## 📝 基础用法

### 创建Goroutine
```go
// 最简单的goroutine
go func() {
    fmt.Println("Hello from goroutine")
}()

// 带参数的goroutine
go func(msg string) {
    fmt.Println(msg)
}("Hello World")
```

### Channel基础
```go
// 创建channel
ch := make(chan int)        // 无缓冲channel
ch := make(chan int, 100)   // 缓冲channel

// 发送和接收
ch <- 42        // 发送
value := <-ch   // 接收

// 关闭channel
close(ch)
```

## 🚀 进阶技巧

### 技巧1：Worker Pool模式
```go
func workerPool(jobs <-chan int, results chan<- int) {
    for j := range jobs {
        results <- j * 2  // 模拟工作
    }
}

func main() {
    const numJobs = 100
    jobs := make(chan int, numJobs)
    results := make(chan int, numJobs)

    // 启动3个worker
    for w := 1; w <= 3; w++ {
        go workerPool(jobs, results)
    }

    // 发送任务
    for j := 1; j <= numJobs; j++ {
        jobs <- j
    }
    close(jobs)

    // 收集结果
    for a := 1; a <= numJobs; a++ {
        <-results
    }
}
```

**说明：**
- 固定数量的worker避免创建过多goroutine
- 使用buffered channel提高性能

### 技巧2：超时控制
```go
func doWithTimeout(timeout time.Duration) error {
    ch := make(chan bool)
    
    go func() {
        // 模拟耗时操作
        time.Sleep(2 * time.Second)
        ch <- true
    }()
    
    select {
    case <-ch:
        return nil
    case <-time.After(timeout):
        return fmt.Errorf("operation timed out")
    }
}
```

### 技巧3：扇出扇入（Fan-out/Fan-in）
```go
// 扇出：一个输入，多个输出
func fanOut(in <-chan int) (<-chan int, <-chan int) {
    out1 := make(chan int)
    out2 := make(chan int)
    
    go func() {
        for val := range in {
            out1 <- val
            out2 <- val
        }
        close(out1)
        close(out2)
    }()
    
    return out1, out2
}

// 扇入：多个输入，一个输出
func fanIn(ch1, ch2 <-chan int) <-chan int {
    out := make(chan int)
    
    go func() {
        for {
            select {
            case v := <-ch1:
                out <- v
            case v := <-ch2:
                out <- v
            }
        }
    }()
    
    return out
}
```

## 📊 最佳实践

### ✅ 推荐做法
1. **明确channel所有权**
   ```go
   // 好的做法：返回只读channel
   func producer() <-chan int {
       ch := make(chan int)
       go func() {
           defer close(ch)
           for i := 0; i < 10; i++ {
               ch <- i
           }
       }()
       return ch
   }
   ```

2. **使用context控制生命周期**
   ```go
   func worker(ctx context.Context) {
       for {
           select {
           case <-ctx.Done():
               return
           default:
               // 执行工作
           }
       }
   }
   ```

### ❌ 避免踩坑
1. **向已关闭的channel发送数据**
   ```go
   // 错误：会panic
   ch := make(chan int)
   close(ch)
   ch <- 42  // panic!
   
   // 正确：确保只有发送方关闭channel
   ```

2. **goroutine泄露**
   ```go
   // 错误：goroutine永远阻塞
   func leak() {
       ch := make(chan int)
       go func() {
           val := <-ch  // 永远等待
           fmt.Println(val)
       }()
       // ch没有被使用
   }
   ```

## 🔍 深入理解

### 原理解析
> 底层是如何工作的

Go调度器采用M:N模型，M个goroutine映射到N个OS线程上。调度器使用工作窃取算法实现负载均衡。

### 性能考虑
- Goroutine创建：~2KB初始栈
- Channel操作：无锁实现，性能优异
- 上下文切换：纳秒级别

### 与其他技术对比
| 特性 | Go Channel | Java Thread | Python asyncio |
|------|------------|-------------|----------------|
| 性能 | 极高 | 高 | 中 |
| 易用性 | 高 | 中 | 中 |
| 生态 | 优秀 | 优秀 | 良好 |

## 🛠 实战案例

### 案例：并发爬虫
**需求：** 并发爬取多个URL，限制并发数

**实现：**
```go
type Result struct {
    URL  string
    Body string
    Err  error
}

func crawl(urls []string, concurrency int) []Result {
    urlChan := make(chan string)
    resultChan := make(chan Result)
    
    // 启动worker
    for i := 0; i < concurrency; i++ {
        go func() {
            for url := range urlChan {
                body, err := fetch(url)
                resultChan <- Result{url, body, err}
            }
        }()
    }
    
    // 发送URL
    go func() {
        for _, url := range urls {
            urlChan <- url
        }
        close(urlChan)
    }()
    
    // 收集结果
    results := make([]Result, 0, len(urls))
    for i := 0; i < len(urls); i++ {
        results = append(results, <-resultChan)
    }
    
    return results
}
```

**要点：**
- 使用worker pool控制并发数
- 分离生产者和消费者

## 🐛 常见问题

### Q1: 如何选择channel缓冲大小？
**问题描述：** 不确定应该使用无缓冲还是有缓冲channel

**解决方案：**
- 默认使用无缓冲channel（同步）
- 当发送方和接收方速率不匹配时使用缓冲
- 缓冲大小根据实际负载测试确定

### Q2: 如何优雅关闭多个goroutine？
**问题描述：** 程序退出时需要等待所有goroutine完成

**解决方案：**
```go
var wg sync.WaitGroup

for i := 0; i < workers; i++ {
    wg.Add(1)
    go func() {
        defer wg.Done()
        // 工作逻辑
    }()
}

wg.Wait() // 等待所有goroutine完成
```

## 📚 学习资源

### 官方资源
- [Go Concurrency Patterns](https://go.dev/blog/pipelines)
- [Effective Go - Concurrency](https://go.dev/doc/effective_go#concurrency)
- [Go Memory Model](https://go.dev/ref/mem)

### 社区资源
- [Go并发编程实战](https://github.com/golang/go/wiki/LearnConcurrency)
- [Visualizing Concurrency in Go](https://divan.dev/posts/go_concurrency_visualize/)

### 相关技术
- [[Go-Context-Best-Practices]]
- [[Go-Sync-Package-Deep-Dive]]
- [[Distributed-Systems-Patterns]]

## 🏷️ 标签云
`go` `concurrency` `goroutine` `channel` `patterns` `csp`

## 📝 更新日志
- 2024-12-15: 初始创建
- 2024-12-17: 添加实战案例和性能分析

---

## 速查表

### 常用模式
```go
// 1. 生产者-消费者
ch := make(chan int)
go producer(ch)
consumer(ch)

// 2. 工作池
jobs := make(chan Job, 100)
for w := 0; w < workers; w++ {
    go worker(jobs)
}

// 3. 扇出扇入
in := make(chan int)
out1, out2 := fanOut(in)
result := fanIn(out1, out2)

// 4. 超时控制
select {
case res := <-ch:
    // 处理结果
case <-time.After(timeout):
    // 超时处理
}

// 5. 退出信号
quit := make(chan struct{})
close(quit) // 广播退出
```

---
*如有错误或补充，欢迎更新此文档*