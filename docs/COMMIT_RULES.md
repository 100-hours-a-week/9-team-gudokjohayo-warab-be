- `<type>` → **작업의 성격을 나타냄**.
- `[scope]` → **선택적, 변경된 모듈/파일명** (`ex: db, security, api`)
- `<commit message>` → **작업 내용을 간결하게 설명**.

##  1. 타입별 설명
| Type | 의미 | 버전 영향 |
|------|------|----------|
| `feat` | 새로운 기능 추가 | Minor (`x.Y.0`) |
| `fix` | 버그 수정 | Patch (`x.y.Z`) |
| `docs` | 문서 변경 | Patch |
| `refactor` | 코드 리팩토링 (기능 변경 없음) | Patch |
| `style` | 코드 스타일 변경 (기능 변경 없음) | Patch |
| `test` | 테스트 코드 추가 또는 변경 | No Release |
| `chore` | 빌드 및 설정 변경 | No Release |
| `deploy` | 배포 관련 작업 | Patch |
| `BREAKING CHANGE` | 기존 기능 변경 (하위 호환성 깨짐) | Major (`X.0.0`) |

##  2. 예시
feat(api): add user authentication

fix(db): resolve PostgreSQL connection issue

docs: update API documentation

refactor(service): optimize game filtering logic

style: reformat code to improve readability

test(auth): improve test coverage for login API

chore(dependencies): update Spring Boot version

deploy: update Dockerfile configuration

BREAKING CHANGE: modify authentication method to OAuth2

##  3. 추가 규칙
- **커밋 메시지는 현재형으로 작성**
- **50자 이내로 간결하게**
- **추가 설명이 필요하면 본문(`body`) 작성 가능**
- **CI/CD 자동화를 위한 `[skip ci]` 옵션 사용 가능**

##  4. 참고
- [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/)