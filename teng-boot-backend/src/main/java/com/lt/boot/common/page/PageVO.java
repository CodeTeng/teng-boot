package com.lt.boot.common.page;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lt.boot.utils.CollUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分页结果")
public class PageVO<T> {
    @Schema(description = "总条数")
    protected Long total;
    @Schema(description = "总页码数")
    protected Long pages;
    @Schema(description = "当前页数据")
    protected List<T> list;

    public static <T> PageVO<T> empty(Long total, Long pages) {
        return new PageVO<>(total, pages, CollUtils.emptyList());
    }

    public static <T> PageVO<T> empty(Page<?> page) {
        return new PageVO<>(page.getTotal(), page.getPages(), CollUtils.emptyList());
    }

    public static <T> PageVO<T> of(Page<T> page) {
        if (page == null) {
            return new PageVO<>();
        }
        if (CollUtils.isEmpty(page.getRecords())) {
            return empty(page);
        }
        return new PageVO<>(page.getTotal(), page.getPages(), page.getRecords());
    }

    public static <T, R> PageVO<T> of(Page<R> page, Function<R, T> mapper) {
        if (page == null) {
            return new PageVO<>();
        }
        if (CollUtils.isEmpty(page.getRecords())) {
            return empty(page);
        }
        return new PageVO<>(page.getTotal(), page.getPages(),
                page.getRecords().stream().map(mapper).collect(Collectors.toList()));
    }

    public static <T> PageVO<T> of(Page<?> page, List<T> list) {
        return new PageVO<>(page.getTotal(), page.getPages(), list);
    }

    public static <T, R> PageVO<T> of(Page<R> page, Class<T> clazz) {
        List<T> voList = page.getRecords().stream().map(record -> {
            try {
                T vo = clazz.getDeclaredConstructor().newInstance();
                BeanUtils.copyProperties(record, vo);
                return vo;
            } catch (Exception e) {
                throw new RuntimeException("PageVO of error", e);
            }
        }).collect(Collectors.toList());
        return new PageVO<>(page.getTotal(), page.getPages(), voList);
    }

    @Schema(hidden = true)
    @JsonIgnore
    public boolean isEmpty() {
        return list == null || list.isEmpty();
    }
}
