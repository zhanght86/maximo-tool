# Maximo 工具集合
主要包括以下功能：
- **Maximo功能迁移** 

## Maximo功能迁移
- **数据库配置** ：导入导出数据库配置相关内容；
- **域迁移** ： 导出域相关的配置
- **应用程序设计器** ：导入导出应用程序设计器相关内容；
- **代码编译复制** ：将源代码编译后复制到项目相应目录中

### 数据库配置
主要功能有两个：
1. 将指定的数据库配置中的对象导出为归档文件；
2. 将导出的归档文件导入到指定的系统中并自动配置数据库变更。

### 域迁移

### 应用程序设计器
主要功能有两个：
1. 将指定的应用程序设计器中的应用程序导出为归档文件；
2. 将导出的归档文件导入到指定的系统中包括导入系统的XML。

## 参数说明
- **-option**：需要执行什么操作，包括： 1. exportdbconfig (导出数据库配置) ; 2. importdbconfig (导入数据库配置)
- **-maximopath**：maximo的发布包的路径
- **-packagepath**：数据导出的路径
- **-importpath**：数据导入的路径

## Ant脚本参数说明