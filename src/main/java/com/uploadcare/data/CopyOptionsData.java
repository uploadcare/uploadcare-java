package com.uploadcare.data;

import com.fasterxml.jackson.annotation.JsonInclude;

public class CopyOptionsData {

    public String source;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String target;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Boolean store;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Boolean makePublic;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String pattern;

}
