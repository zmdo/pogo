## pogo 插件使用方法
### 安装方法
直接将pogo.jar拖入 Goland 然后重启即可。关于PO对象结构体的生成方法，请参考下面的介绍。
#### 方法一：自动生成
使用提供的pomaker进行自动生成：

1. 请先下载 https://github.com/zmdo/pomaker
2. 切换到pomaker目录下
3. 打开pomaker.py 对里面的参数进行修改

```python
# pomaker.py中需要修改的参数
# 项目根目录地址
PROJECT_ROOT_PATH = "E:\\goland workspaces\\project"

# PO对象在项目根目录下的输出地址
OUTPUT_PATH = "share\\potest"

# SQL文件名
SQL_FILE = "aam.sql"

# 表前缀
TABLE_PREFIX = "aam_"

# 生成文件的包名
PACKAGE_NAME = 'po'
```
4. 运行 `python pomaker.py`

#### 方法二： 手撸代码
按照pomaker文件下的README.md的命名规则介绍，手动撸一份代码。如果不愿意按照其命名规则进行构建，那至少保证：

1. 文件要以 .po.go 为后缀 ，例如：“demo.po.go”
2. 文件中的PO对象结构体要以PO作为后辍结尾，例如：“DemoPO”
3. 文件有且只能存在一个PO对象结构体(主要是因为目前只能读取一个PO对象结构体)

这里建议选择方法一。

---
## How to use pogo plug-in
### Installation method
Direct pogo.jar Drag into GoLand and restart. For the generation method of Po object structure, please refer to the following introduction.
#### Method 1: automatic generation
Use the pomaker provided for automatic generation:
1. Please download : https://github.com/zmdo/pomaker
2. Switch to the pomaker directory
3. Open pomaker.py Modify the parameters inside

```python

# pomaker.py Parameters to be modified in

# Project root address
PROJECT_ROOT_PATH = "E:\\goland workspaces\\project"

# The output address of Po object in the project root directory
OUTPUT_PATH = "share\\potest"

# SQL file name
SQL_FILE = " aam.sql "

# Table prefix
TABLE_PREFIX = "aam_"

# The package name of the build file
PACKAGE_NAME = 'po'

```
4. Run ` Python pomaker.py `

#### Method 2: hand code
Follow the pomaker [README.md](https://github.com/zmdo/pomaker/blob/master/README.md) The naming rules, manual roll a code. If you are not willing to build according to its naming rules, at least ensure that:
1. The go file should be in the form of po.go Is a suffix, example:“ demo.po.go ”
2. The Po object structure in the file should end with PO, example: "DemoPO"
3. The file has and can only have one PO structure (mainly because only one PO structure can be read at present)

It is suggested to choose method one.


