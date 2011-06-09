package cn.bc.web.ui;

public interface Render {
	/**
	 * 将组件的渲染文本写入容器
	 * @param main 以初始化好的字符容器
	 * @return
	 */
	StringBuffer render(StringBuffer main);
}
