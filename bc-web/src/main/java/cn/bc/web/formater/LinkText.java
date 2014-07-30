package cn.bc.web.formater;

/**
 * 带链接文字
 * Created by dragon on 2014/7/15.
 */
public class LinkText {
    private String id;
    private String clazz = "bc-link";
    private String url;// 链接
    private String navTitle;// 任务栏的名称
    private String title;// 鼠标提示信息
    private String moduleType;// 模块类型
    private String moduleId;// 模块ID
    private String label;// 显示的名称
    private String style;// css样式控制
    private String tag = "a";

    public LinkText() {
    }

    public LinkText(String moduleType, String moduleId, String label, String url) {
        this.moduleType = moduleType;
        this.moduleId = moduleId;
        this.label = label;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public LinkText setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public LinkText setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getModuleType() {
        return moduleType;
    }

    public LinkText setModuleType(String moduleType) {
        this.moduleType = moduleType;
        return this;
    }

    public String getModuleId() {
        return moduleId;
    }

    public LinkText setModuleId(String moduleId) {
        this.moduleId = moduleId;
        return this;
    }

    public String getId() {
        return id;
    }

    public LinkText setId(String id) {
        this.id = id;
        return this;
    }

    public String getClazz() {
        return clazz;
    }

    public LinkText setClazz(String clazz) {
        this.clazz = clazz;
        return this;
    }

    public String getNavTitle() {
        return navTitle;
    }

    public LinkText setNavTitle(String navTitle) {
        this.navTitle = navTitle;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public LinkText setLabel(String label) {
        this.label = label;
        return this;
    }

    public String getStyle() {
        return style;
    }

    public LinkText setStyle(String style) {
        this.style = style;
        return this;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * 构建图标的Html
     * @return
     */
    public String toString() {
        StringBuffer c = new StringBuffer();
        c.append("<" + this.getTag());

        // css class
        String clazz = this.getClazz();
        if (clazz != null) c.append(" class=\"" + clazz + "\"");

        // url
        String url = this.getUrl();
        if (url != null) {
            c.append(" href=\"" + url + "\"");
        }else{
            c.append(" href=\"#\"");
        }

        // id
        String id = this.getId();
        if (id != null) c.append(" id=\"" + id + "\"");

        // moduleType
        String moduleType = this.getModuleType();
        if (moduleType != null) c.append(" data-mtype=\"" + moduleType + "\"");

        // moduleId
        String moduleId = this.getModuleId();
        if (moduleId != null) c.append(" data-mid='" + moduleId + "'");

        // navTitle
        String navTitle = this.getNavTitle();
        String label = this.getLabel();
        if (navTitle == null) navTitle = label;
        if (navTitle != null) {
            c.append(" data-title=\"" + navTitle + "\"");
        }

        // tTitle
        String title = this.getTitle();
        if (title == null) title = label;
        if (title != null) {
            c.append(" title=\"" + title + "\"");
        }

        // style
        String style = this.getStyle();
        if (style != null) c.append(" style=\"" + style + "\"");

        c.append(">");

        // label
        if (label != null) c.append(label);

        c.append("</" + this.getTag() + ">");

        return c.toString();
    }
}
