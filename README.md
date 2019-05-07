# WebSocketCall

1. WebSocket 长连接
2. Android 进程保活
3. module 组件化开发
    
    3.1 module_keepalive java文件(xml 文件)如何读取 gradle.properties 的 isModuleRun 值 
    
        目前 账户同步写死是 ""com.cl.cloud"，需要实时读取对应的包名进行替换
        
        思路1: java 文件读取 gradle.properties 包名
        思路2: build.gradle applicationId 根据 gradle.properties 实时变换，读取 applicationId，代码读取 Androidmanifest.xml meta_data
        
# CloudService