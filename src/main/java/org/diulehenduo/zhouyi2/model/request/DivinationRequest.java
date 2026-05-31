package org.diulehenduo.zhouyi2.model.request;

import jakarta.validation.constraints.NotBlank;

/**
 * 占卜请求体
 */
public class DivinationRequest {

    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotBlank(message = "测算事由不能为空")
    private String matter;

    public DivinationRequest() {}

    public DivinationRequest(String name, String matter) {
        this.name = name;
        this.matter = matter;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getMatter() { return matter; }

    public void setMatter(String matter) { this.matter = matter; }
}
