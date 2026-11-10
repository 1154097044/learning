package com.xy.learning.week2.todo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


/**
 * 更新 Todo 的请求体。@NotNull 用于布尔/包装类型（done 允许显式传 false）。
 */
public record TodoUpdateRequest(

        @NotBlank(message = "标题不能为空")
        @Size(max = 100, message = "标题不能超过100个字符")
        String title,

        @Size(max = 500, message = "描述不能超过500个字符")
        String description,

        @NotNull(message = "done 不能为 null")
        Boolean done
) {

        @Override
        public String toString() {
                return "TodoUpdateRequest{" +
                        "title='" + title + '\'' +
                        ", description='" + description + '\'' +
                        ", done=" + done +
                        '}';
        }
}
