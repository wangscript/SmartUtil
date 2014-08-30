package iminto.db;

/**
 * 分页对象
 * 
 * @author li (limw@w.cn)
 * @version 0.1.2 (2012-06-17)
 */
public class Page {
    /**
     * 默认PageSize为20,可以在配置文件中修改
     */
    public static final Integer DEFAULT_SIZE =20;

    private Integer pageNumber = 1;// 当前页数

    private Integer pageSize = DEFAULT_SIZE;// 单页记录数

    private Integer recordCount = 0;// 总记录数

    private Boolean count = true;// 是否进行count查询

    /**
     * 默认构造函数
     */
    public Page() {}

    /**
     * 带有pageNumber,pageSize两个参数的构造函数
     */
    public Page(Integer pageNumber, Integer pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    /**
     * 是否进行count查询
     */
    public Boolean count() {
        return this.count;
    }

    /**
     * 设置是否进行count查询
     */
    public Page count(Boolean flag) {
        this.count = flag;
        return this;
    }

    /**
     * 返回当前页码
     */
    public Integer getPageNumber() {
        return this.pageNumber;
    }

    /**
     * 设置页码,跳转到第 pn 页
     */
    public Page setPageNumber(Integer pn) {
        this.pageNumber = (null == pn || pn < 1) ? 1 : pn;
        return this;
    }

    /**
     * 返回单页记录数
     */
    public Integer getPageSize() {
        return this.pageSize;
    }

    /**
     * 设置单页记录数,如果不想是用默认值 20 的话
     */
    public Page setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    /**
     * 设置总记录数,会根据recordCount同步设置pageCount
     */
    public Page setRecordCount(Integer recordCount) {
        this.recordCount = (null == recordCount || recordCount < 1) ? 0 : recordCount;
        return this;
    }

    /**
     * 返回总记录数
     */
    public Integer getRecordCount() {
        return this.recordCount;
    }

    /**
     * 得到总页数
     */
    public Integer getPageCount() {
        return (getRecordCount() / getPageSize()) + ((0 == getRecordCount() % getPageSize()) ? 0 : 1);
    }

    /**
     * 返回上一页pageNumber,经过判断,使不会小于 1
     */
    public Integer getPrevious() {
        return getPageNumber() > 1 ? getPageNumber() - 1 : 1;
    }

    /**
     * 返回下一页pageNumber,经过判断,使不会大于getPageCount
     */
    public Integer getNext() {
        return getPageNumber() + 1 < getPageCount() ? getPageNumber() + 1 : getPageCount();
    }

    /**
     * 返回当前页的第一条记录的序列号
     */
    public Integer getFrom() {
        return (getPageNumber() - 1) * getPageSize();
    }
}