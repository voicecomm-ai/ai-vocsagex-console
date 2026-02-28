package cn.voicecomm.ai.voicesagex.console.user.util;

import cn.voicecomm.ai.voicesagex.console.api.dto.PagingReqDto;
import cn.voicecomm.ai.voicesagex.console.api.dto.PagingRespDto;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PageUtil {

  public static <T> Page<T> of(PagingReqDto dto) {
    return new Page<>(dto.getCurrent(), dto.getSize());
  }

  public static <T, R> PagingRespDto<R> of(Page<T> page, Function<List<T>, List<R>> function) {
    return PagingRespDto.<R>builder()
      .current(page.getCurrent())
      .size(page.getSize())
      .total(page.getTotal())
      .records(function.apply(page.getRecords()))
      .build();
  }

  public static <T> Page<T> startPage(List<T> list, Integer pageNum, Integer pageSize) {
    if (list == null) {
      return null;
    }
    if (list.isEmpty()) {
      return null;
    }

    Integer count = list.size(); // 记录总数
    Integer pageCount = 0; // 页数
    if (count % pageSize == 0) {
      pageCount = count / pageSize;
    } else {
      pageCount = count / pageSize + 1;
    }

    int fromIndex = 0; // 开始索引
    int toIndex = 0; // 结束索引

    if (!Objects.equals(pageNum, pageCount)) {
      fromIndex = (pageNum - 1) * pageSize;
      toIndex = fromIndex + pageSize;
    } else {
      fromIndex = (pageNum - 1) * pageSize;
      toIndex = count;
    }

    if (fromIndex > count - 1) {
      // 如果起始size大于总数
      return new Page<>();
    }
    if (toIndex > count) {
      toIndex = count;
    }
    List<T> pageList = list.subList(fromIndex, toIndex);

    Page<T> objectPage = new Page<>(pageNum, pageSize);
    objectPage.setRecords(pageList);
    return objectPage;
  }
}
