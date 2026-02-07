package com.utime.memoBom.board.service.impl;

import java.net.URI;

public final class SimpleLinkRenderer {

    // 너무 긴 입력 방지(원하는 값으로 조정)
    private static final int MAX_LEN = 50_000;

    // URL 끝에서 제거할 문장부호(필요하면 추가)
    private static final String TRAIL_PUNCT = ".,;:!?)]]}”’\"'";

    private SimpleLinkRenderer() {}

    /**
     * 사용자 입력 텍스트를 안전한 HTML로 렌더링한다.
     * - HTML Escape
     * - https:// URL 자동 링크
     * - \n -> <br>
     *
     * @param raw 사용자 원문
     * @param appOrigin 동일 오리진 판별에 사용할 기준(예: "https://springbom.duckdns.org")
     */
    public static String render(String raw, String appOrigin) {
        if (raw == null || raw.isEmpty()) return "";
        if (raw.length() > MAX_LEN) raw = raw.substring(0, MAX_LEN);

        // 1) escape
        String escaped = escapeHtml(raw);

        // 2) linkify (https://만)
        String linked = linkifyHttps(escaped, appOrigin);

        // 3) newline
        return linked.replace("\n", "<br>");
    }

    private static String linkifyHttps(String s, String appOrigin) {
        StringBuilder out = new StringBuilder((int)(s.length() * 1.1));

        int i = 0;
        while (i < s.length()) {
            int idx = s.indexOf("https://", i);
            if (idx < 0) {
                out.append(s, i, s.length());
                break;
            }

            // https:// 전까지 복사
            out.append(s, i, idx);

            // URL 후보 추출: 공백/개행/< 까지
//            int j = idx;
            int end = s.length();
            for (int k = idx; k < s.length(); k++) {
                char c = s.charAt(k);
                if (c == ' ' || c == '\n' || c == '\t' || c == '<') { // <는 escape된 텍스트엔 거의 안 나오지만 안전장치
                    end = k;
                    break;
                }
            }

            String candidate = s.substring(idx, end);

            // 끝 문장부호 제거
            String url = stripTrailingPunct(candidate);

            // url 뒤에 남은 문장부호(있으면 다시 붙여야 함)
            String tail = candidate.substring(url.length());

            // URL 검증(최소)
            String normalized = normalizeHttps(url);
            if (normalized == null) {
                // 링크로 만들지 않고 원문 출력
                out.append(candidate);
                i = end;
                continue;
            }

            boolean sameOrigin = isSameOrigin(normalized, appOrigin);

            // a 태그 생성
            // - sameOrigin: 내부 이동(그냥 href)
            // - external: data-external=1 같은 마킹을 해두면 JS로 확인 모달 처리 가능
            out.append(buildAnchor(normalized, sameOrigin));

            // 제거했던 문장부호 복원
            out.append(tail);

            i = end;
        }

        return out.toString();
    }

    private static String buildAnchor(String href, boolean sameOrigin) {
        // 앱 UX 때문에 external은 마킹만 해두고, 실제 동작은 프론트에서 제어 추천
        String rel = "noopener noreferrer nofollow";
        if (sameOrigin) {
            return "<a class=\"text-sky-600 underline hover:text-sky-700\" " +
                    "href=\"" + href + "\" target=\"_self\" rel=\"" + rel + "\">" +
                    href + "</a>";
        }
        return "<a class=\"text-sky-600 underline hover:text-sky-700\" " +
                "href=\"" + href + "\" target=\"_self\" rel=\"" + rel + "\" " +
                "data-external=\"1\" data-url=\"" + href + "\">" +
                href + "</a>";
    }

    private static boolean isSameOrigin(String url, String appOrigin) {
        if (appOrigin == null || appOrigin.isBlank()) return false;
        try {
            URI u = URI.create(url);
            URI o = URI.create(appOrigin);
            return eq(u.getScheme(), o.getScheme()) && eq(u.getHost(), o.getHost()) && port(u) == port(o);
        } catch (Exception e) {
            return false;
        }
    }

    private static int port(URI u) {
        int p = u.getPort();
        if (p != -1) return p;
        // default ports
        return "https".equalsIgnoreCase(u.getScheme()) ? 443 : 80;
    }

    private static boolean eq(String a, String b) {
        if (a == null) return b == null;
        return a.equalsIgnoreCase(b);
    }

    private static String normalizeHttps(String v) {
        try {
            URI uri = URI.create(v);
            if (!"https".equalsIgnoreCase(uri.getScheme())) return null;
            if (uri.getHost() == null) return null;
            return uri.toString();
        } catch (Exception e) {
            return null;
        }
    }

    private static String stripTrailingPunct(String s) {
        int end = s.length();
        while (end > 0) {
            char c = s.charAt(end - 1);
            if (TRAIL_PUNCT.indexOf(c) >= 0) end--;
            else break;
        }
        return s.substring(0, end);
    }

    private static String escapeHtml(String s) {
        StringBuilder out = new StringBuilder((int)(s.length() * 1.1));
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '&' -> out.append("&amp;");
                case '<' -> out.append("&lt;");
                case '>' -> out.append("&gt;");
                case '"' -> out.append("&quot;");
                case '\'' -> out.append("&#39;");
                default -> out.append(c);
            }
        }
        return out.toString();
    }
}

