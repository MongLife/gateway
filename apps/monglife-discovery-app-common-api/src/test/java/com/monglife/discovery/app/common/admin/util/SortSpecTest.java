package com.monglife.discovery.app.common.admin.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SortSpec 파싱")
class SortSpecTest {

    private static final Set<String> ALLOWED = Set.of("accountId", "createdAt");

    @Test
    @DisplayName("field,asc 를 읽는다")
    void parse_asc() {
        SortSpec spec = SortSpec.parse("createdAt,asc", ALLOWED, "accountId", true);
        assertThat(spec.key()).isEqualTo("createdAt");
        assertThat(spec.desc()).isFalse();
    }

    @Test
    @DisplayName("방향이 없으면 desc")
    void parse_defaultDirection() {
        assertThat(SortSpec.parse("createdAt", ALLOWED, "accountId", true).desc()).isTrue();
    }

    @Test
    @DisplayName("허용되지 않은 필드는 기본값으로 떨어진다")
    void parse_notAllowed() {
        SortSpec spec = SortSpec.parse("password,asc", ALLOWED, "accountId", true);
        assertThat(spec.key()).isEqualTo("accountId");
        assertThat(spec.desc()).isTrue();
    }

    @Test
    @DisplayName("비어 있으면 기본값")
    void parse_blank() {
        assertThat(SortSpec.parse(null, ALLOWED, "accountId", false)).isEqualTo(new SortSpec("accountId", false));
        assertThat(SortSpec.parse("  ", ALLOWED, "accountId", false)).isEqualTo(new SortSpec("accountId", false));
    }
}
