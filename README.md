# samplePJ-TODO

Eclipse と Spring Boot で作成した、個人向けのタスク管理アプリです。
研修課題をベースに、実務レベルの設計・実装経験を積むための自主学習として拡張を続けています。

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-6DB33F?logo=springboot&logoColor=white)
![MyBatis](https://img.shields.io/badge/MyBatis-000000?logo=apache&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?logo=postgresql&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?logo=thymeleaf&logoColor=white)

---

## 🚀 主な機能

### 画面機能
- タスクの一覧表示(ページング対応)
- タスクの新規作成・編集・削除
- 期間指定によるCSVエクスポート
- ログイン・ユーザー登録

### WebAPI機能(実装中)
画面表示に依存しない、JSON形式でのタスク管理APIを段階的に実装しています。

| メソッド | エンドポイント | 内容 | 状態 |
|---|---|---|---|
| GET | `/api/tasks` | タスク一覧取得(ページング対応) | ✅ 実装済み |
| GET | `/api/tasks/{id}` | タスク単体取得 | ✅ 実装済み |
| PUT | `/api/tasks/{id}` | タスク更新 | ✅ 実装済み |
| DELETE | `/api/tasks/{id}` | タスク削除 | ✅ 実装済み |
| POST | `/api/tasks` | タスク新規作成 | 🚧 未着手 |
| GET | `/api/tasks/export` | CSVエクスポート | 🚧 未着手 |

APIはすべて、未ログイン時は `401 Unauthorized`、対象タスクが存在しない・他人のタスクの場合は `404 Not Found` を返す設計で統一しています(存在しないIDと他人のタスクを区別しないことで、IDOR的な情報漏洩を防止)。

---

## 🛠 使用技術

| 分類 | 技術 |
|---|---|
| 言語 | Java 21 |
| フレームワーク | Spring Boot 4.1.0 |
| O/Rマッパー | MyBatis |
| データベース | PostgreSQL |
| テンプレートエンジン | Thymeleaf |
| フロントエンド | HTML / CSS / JavaScript(Fetch API) |
| テスト | JUnit |
| ビルドツール | Gradle |
| バージョン管理 | Git / GitHub |
| IDE | Eclipse(EGit) / VSCode |

---

## 📐 設計上のポイント

- **画面用APIとWebAPIの役割分離**：`TaskController`(画面表示用)と`TaskApiController`(JSON API用)を意図的に分離し、認証チェックやエラーハンドリングの方式もそれぞれに最適化
- **IDOR対策**：全てのデータアクセスでログインユーザーとの紐付けをチェックし、他人のタスクへの不正アクセスを防止
- **DTOによる入力制御**：APIの更新処理では`TaskUpdateRequest`などの専用DTOを介し、エンティティを直接公開しないことで、意図しない項目の書き換えを防止
- **段階的なAPI移行**：既存の画面機能を壊さずに残しながら、一覧・詳細取得・更新・削除の各機能をAPI化する形で移行を進行中

---

## 📂 セットアップ

### 前提条件
- Java 21
- PostgreSQL

### 手順

1. リポジトリをクローン
   ```bash
   git clone <このリポジトリのURL>
   ```

2. PostgreSQLにデータベースを作成し、接続情報を `application.properties` に設定

3. アプリケーションを起動
   ```bash
   ./gradlew bootRun
   ```

4. ブラウザで `http://localhost:8080` にアクセス

5. `/register` からアカウントを作成してログイン

### サンプルデータを使う場合
`sample_tasks_test1_test2.sql` を使うと、動作確認用のサンプルタスクを投入できます。
使用前に `/register` で `test1`・`test2` の2アカウントを作成しておいてください。

---

## 🧪 テスト方法

```bash
./gradlew test
```

テストは、開発用DB（`todo_app`）とは別の、テスト専用DB（`todo_test`）に接続して実行されます。
実行前に `todo_test` を作成しておいてください。

```bash
createdb todo_test
```

### DB接続関係のpropertiesについて

- `src/main/resources/application.properties`：開発用（`todo_app`）
- `src/test/resources/application.properties`：テスト用（`todo_test`）。テスト実行時はこちらが優先される

**注意**：`src/test/resources` の階層は厳密です。`src/test/java/resources` のように1階層でもズレると認識されず、開発用の設定がそのまま使われてしまいます。

```
src/test/
  ├── java/
  └── resources/
      ├── application.properties
      └── test-schema.sql
```

---

## 👥 作成者

- tomonariY