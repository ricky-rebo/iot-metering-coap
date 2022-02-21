package it.unimore.dipi.iot.metering.server.resources.model;

public class ResourceURIDescriptor {
    private String uri;
    private String rt;
    private String _if;
    private String title;

    public ResourceURIDescriptor(String uri, String rt, String _if, String title) {
        this.uri = uri;
        this.rt = rt;
        this._if = _if;
        this.title = title;
    }

    public ResourceURIDescriptor() {
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getRt() {
        return rt;
    }

    public void setRt(String rt) {
        this.rt = rt;
    }

    public String getIf() {
        return _if;
    }

    public void setIf(String _if) {
        this._if = _if;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // IMPROVE add ct

    @Override
    public String toString() {
        return String.format("<%s>;rt=\"%s\";if=\"%s\";title=\"%s\"", uri, rt, _if, title);
    }
}
