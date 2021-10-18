# 报表管理

## 报表模板配置规范

```txt
{
  // 报表类型：
  // 1. data - 数据报表，不配置时默认为此值
  // 2. chart - 图表报表
  // 3. no-ui-sql - 代表没有UI界面只能后台执行的纯原生 sql 报表
  type: "..."

  // 报表 sql：
  // 1. 格式为 `tpl:${模板编码}` 代表使用模板的内容作为 sql
  // 2. 否则视为纯 sql 代码使用
  sql: "tpl:car.list.sql", 
  // sql 的查询方式：
  // 1. jpa - jpql
  // 2. jdbc - 原生 sql
  "queryType": "jpa|jdbc"
  // 导出 Excel 所使用的模板编码
  export: "tpl:car.list.excel", 
  // 生成的历史报表附件的文件扩展名，默认为 xls
  extension: "xls",
  // 额外配置的 Excel 模板参数，标准 json 对象格式即可
  exportExtParams: {...},

  // 通过 spel 表达式配置获取报表文件流
  // 如果配置了 columns 则忽略此配置
  "stream": "..."
  // UI 的列配置
  columns: [
    {type: "id",id: "car.id", width: 40, el:"id"},
    {id: "registerDate", label: "登记日期", width: 90},
    ...
  ],
  // UI 的高级搜索
  // 1. 带 `action:` 前缀代表 strust 的 action 路径
  // 2. 带 `jsp:` 前缀代表 jsp 文件路径
  condition: "action:bc-business/cars/conditions2", 
  // UI 的模糊搜索字段，多个用逗号链接
  search: "a.c1,a.c2,...", 
  width: 900,  // UI 宽度
  height: 490, // UI 高度
  paging: true // UI 是否分页
}
```