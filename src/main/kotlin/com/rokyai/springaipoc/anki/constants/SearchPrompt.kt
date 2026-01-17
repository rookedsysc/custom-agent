package com.rokyai.springaipoc.anki.constants

/**
 * ⚠️ 현재 미사용 프롬프트
 *
 * ## 생성 배경
 * Claude Code와 Context7 MCP 서버를 활용한 기술 검색 시스템을 위해 설계되었습니다.
 * 이 프롬프트는 Claude가 WebSearch와 Context7 도구를 직접 호출하여
 * 공식 문서와 최신 정보를 기반으로 답변을 생성하도록 유도합니다.
 *
 * ## 미사용 이유
 * AnkiMakerService에서 Gemini Deep Research API를 사용하도록 변경되었습니다.
 * Gemini Deep Research는 자체적으로 다단계 연구를 계획하고 실행하는 자율 시스템으로,
 * 외부 프롬프트를 주입하면 오히려 성능이 저하될 수 있습니다.
 *
 * - Gemini Deep Research: 자율적 연구 알고리즘 (현재 사용)
 * - SEARCH_PROMPT: Claude + Context7 기반 검색 (미사용)
 *
 * ## 재사용 가능 시나리오
 * 향후 OpenAI ChatClient와 Context7을 조합한 검색 기능으로 전환할 경우,
 * 이 프롬프트를 AnkiMakerService.search() 메서드에 적용할 수 있습니다.
 *
 * @see com.rokyai.springaipoc.anki.service.AnkiMakerService.search
 */
@Suppress("unused")
const val SEARCH_PROMPT = """
# 기술 내용 검색 및 답변 프롬프트

아래 지침에 따라 기술 내용을 검색하고 답변을 작성해줘. Use Context7

## 명령어 실행 방식

### 실행 흐름
1. 검색 우선순위에 따라 정보 수집 (Arguments 반영)
2. 답변 구조에 따라 작성 (출처 필수)

## 검색 우선순위

1. 인터넷 검색 우선: 잘 알려지지 않은 내용이나 최신 정보는 WebSearch를 먼저 사용
2. Context7 활용: 공식 문서, 라이브러리 사용법, 프레임워크 패턴은 Context7로 검색
3. 출처 표기 필수: 각 내용별로 반드시 출처 링크를 표기

## 답변 구조

### 1. 결론 우선 작성
- 최상단에 핵심 답변을 먼저 제시
- 질문자가 바로 원하는 정보를 얻을 수 있도록 구성

### 2. 세부 내용 전개
- 결론을 뒷받침하는 구체적인 설명
- 질문자의 의도에 적절한 내용만 포함
- 쓸데없는 배경 지식이나 불필요한 설명 제외

### 3. 코드 예시 (필요한 경우만)
- 이해를 돕는 간단한 예시 코드만 포함
- 보일러플레이트나 뻔한 코드는 생략
- 핵심 로직에 집중

### 4. 대안 제시 (있는 경우)
- 간략하게 명시
- 각 대안의 장단점을 1-2줄로 요약

## 작성 스타일

- 블로그 게재 수준: 자연스럽고 부드럽게 읽혀야 함
- 비문 금지: 문법적으로 올바르고 완결된 문장만 사용
- 일관된 어휘: 같은 개념은 동일한 용어로 표현 (예: transaction을 어디서는 영어로, 어디서는 한국어로 쓰지 않기)
- 적절한 존댓말: "~합니다", "~입니다" 체 사용

## 필수 검증 체크리스트

답변 작성 후 반드시 다음 항목을 확인:

### 기술적 정확성
- [ ] 기술적으로 틀린 내용은 없는가?
- [ ] 최신 버전의 정보인가?
- [ ] 공식 문서와 일치하는가?

### 질문 적합성
- [ ] 질문자의 의도에 적절한 답변인가?
- [ ] 불필요한 내용은 제외했는가?
- [ ] 핵심만 간결하게 전달했는가?

### 문서 품질
- [ ] 출처 링크가 모두 표기되어 있는가?
- [ ] 코드 예시는 필요한 것만 포함했는가?
- [ ] 글이 자연스럽게 읽히는가?
- [ ] 비문이나 오타는 없는가?
- [ ] 어휘 사용이 일관적인가?

## 출처 표기 형식

각 내용마다 출처를 명시하고, 실제 원문 인용구를 함께 제공합니다.

```markdown
## 결론
- [핵심 내용 1] [출처명](https://example.com/path1)

> 위 출처에서 "실제 원문 내용 인용" 이라고 명시되어 있음.

- [핵심 내용 2] [출처명](https://example.com/path2)

> 위 출처에서 "실제 원문 내용 인용" 이라고 명시되어 있음.

## 세부 설명
- [상세 내용 1] [참고 자료](https://example.com/ref1)

> 위 출처에서 "관련 원문 인용" 이라고 명시되어 있음.
```

## 예시 구조

```markdown
# React의 useEffect 정리 함수

## 결론
- useEffect의 return 문에 정의한 함수는 컴포넌트가 언마운트되거나 의존성 배열이 변경되기 직전에 실행됩니다 [React 공식 문서 - useEffect](https://react.dev/reference/react/useEffect)

> React 공식 문서에서 "The cleanup function runs before the component is removed from the UI to prevent memory leaks. Additionally, if a component renders multiple times, the previous effect is cleaned up before executing the next effect" 라고 명시되어 있음.

## 사용 시점
- 타이머 정리 시 clearInterval/clearTimeout 사용 [React 공식 문서](https://react.dev/learn/synchronizing-with-effects#step-3-add-cleanup-if-needed)

> React 공식 문서에서 "If your Effect subscribes to something, the cleanup function should unsubscribe" 라고 명시되어 있음.

- 구독 취소 (unsubscribe)
- WebSocket 연결 종료

## 코드 예시
```javascript
useEffect(() => {
  const timer = setInterval(() => console.log('tick'), 1000);

  return () => clearInterval(timer); // 정리 함수
}, []);
```

- 정리 함수는 effect가 다시 실행되기 전에 이전 effect를 정리하는 역할을 합니다 [useEffect Complete Guide](https://overreacted.io/a-complete-guide-to-useeffect/)

> 위 블로그에서 "React cleans up the previous render's effects before running the next render's effects" 라고 명시되어 있음.
```
"""
