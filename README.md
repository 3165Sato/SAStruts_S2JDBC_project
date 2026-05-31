# sastruts_s2jdbc_project

## 概要

このプロジェクトは、Doltengで生成した Seasar2 / SAStruts / S2JDBC 構成のサンプルアプリケーションです。

目的は、レガシーJavaフレームワーク環境に対して、AIによる実装追加・テストコード生成・CI実行を検証することです。

## 技術構成

- Java: 7 または 8
- Framework: Seasar2
- Presentation: SAStruts
- Persistence: S2JDBC
- Server Management: WTP / Servlet 2.5
- Application Server: Tomcat
- Test Framework: S2JUnit / S2JUnit4 を想定

## 起動確認

以下の方法で初回起動確認済みです。

- Eclipse上でTomcatにプロジェクトを追加
- Tomcatを起動
- ブラウザからURL直打ちで初期画面を表示

注意:
Eclipseの `IndexAction.java` 右クリックによるSAStruts起動メニューは使用していません。

## AI / Codex 利用時の注意

このプロジェクトは、Doltengで生成された既存構成を維持することを前提とします。

以下は禁止です。

- Spring Bootへ変換しない
- JUnit 5へ移行しない
- Maven / Gradle 構成を勝手に大きく変更しない
- diconファイル構成を破壊しない
- web.xmlを大きく書き換えない
- SAStruts / Seasar2 / S2JDBC を別フレームワークに置き換えない

## 最初にCodexへ依頼する内容

まずは既存構成を解析し、以下を整理してください。

- Javaバージョン
- 依存ライブラリ
- diconファイル構成
- web.xml構成
- Actionクラスの配置場所
- Serviceクラスの配置場所
- テストクラスの配置場所
- テスト実行方法
- 変更してはいけないファイル

その後、最小のサンプル機能として「請求書ステータス管理」を追加してください。

## サンプル機能: 請求書ステータス管理

### 作成対象

- Invoice
- InvoiceStatus
- InvoiceService
- InvoiceServiceTest

### 要件

- 請求書を登録できる
- 金額が0円以下の場合は例外にする
- 未承認の請求書を承認済みに変更できる
- 承認済み請求書を再承認しようとした場合は例外にする
- 存在しない請求書IDを承認しようとした場合は例外にする

### テスト要件

S2JUnit または S2JUnit4 を使用して、以下のテストを作成する。

- 正常系: 請求書を登録できる
- 正常系: 未承認の請求書を承認済みにできる
- 異常系: 金額が0円の場合は登録できない
- 異常系: 金額がマイナスの場合は登録できない
- 異常系: 承認済み請求書は再承認できない
- 異常系: 存在しない請求書IDは承認できない

## 実装方針

最初はDBを使用せず、インメモリ実装でよいです。

目的は、まず以下を確認することです。

- Seasar2構成を維持したままAIが実装追加できるか
- S2JUnit形式のテストコードをAIが生成できるか
- ローカル環境でテスト実行できるか
- 将来的にGitLab CIで自動テストできるか

## 検証目的

このリポジトリは、会社で検討しているレガシーJava環境におけるAI活用、S2JUnit4テストのCI実行、CI/CD実現性、Excelテストデータ依存からの脱却を個人検証するための環境です。

主な検証目的は以下です。

- Seasar2 / SAStruts / S2JDBC のレガシー構成で、AI生成テストコードがどこまで使えるか検証する
- S2JUnit4テストをGitLab CI上で自動実行できるか検証する
- DBアクセスありのS2JUnit4テストをH2 + S2JDBCで実行できるか検証する
- Excelテストデータに依存しないテストデータ準備方式を比較する

## 検証環境の技術構成

- Java: プロジェクト指定はJava 1.7
- ローカル実行: JDK 8
- Build Tool: Maven 3.6.3
- Framework: Seasar2 2.4.46
- Web Framework: SAStruts 1.0.4-sp9
- Persistence: S2JDBC / S2JDBC-Gen 2.4.46
- Test Framework: S2JUnit4
- Database: H2
- CI: GitLab CI
- Project Template: Dolteng生成プロジェクト

## 検証ステップと達成状況

| Level | 内容 | 状況 |
| --- | --- | --- |
| Level 1 | 単一Service + 複雑な状態遷移 | 完了 |
| Level 2 | 複数Logicに分割 | 完了 |
| Level 3 | Serviceが複数Logicを呼ぶ | 完了 |
| Level 4 | Storeを分離 | 完了 |
| Level 5 | H2 + S2JDBC DBありS2JUnit4テスト | 完了 |
| Level 6-1 | TestDataBuilder方式 | 完了 |
| Level 6-2 | Fixture方式 | 完了 |
| Level 6-3 | SQLファイル方式 | 完了 |
| Level 7 | 意図的バグを入れてAI生成テストが検知できるか評価 | 完了 |

## S2JUnit4 + GitLab CI 検証結果

`@RunWith(Seasar2.class)` を使用したS2JUnit4テストを作成し、ローカル環境で `mvn clean test` が成功することを確認しました。

GitLab CI上でも同じく `mvn clean test` を実行し、S2JUnit4テストが自動実行できることを確認済みです。

Maven 3.9系では `http://maven.seasar.org/maven2` のHTTPリポジトリがブロックされるため、この検証ではMaven 3.6.3を使用しています。JDK 8 + Maven 3.6.3 の組み合わせで安定してテスト実行できることを確認しました。

## AI生成テストの有効性確認

業務ロジックに意図的なバグを混入し、既存のS2JUnit4テストが退行を検知できるかを検証しました。

検証では、状態遷移ルールに反する実装を入れた場合にS2JUnit4テストが失敗し、GitLab CIのパイプライン失敗として検知できることを確認しました。

この結果から、AI生成テストは少なくとも状態遷移や業務ルールの退行検知に有効であることを確認できました。

## DBありS2JUnit4テスト

既存のインメモリ実装である `InvoiceService` は変更せず、DB検証専用の最小機能として `DbInvoiceService` を追加しました。

DBありテストではH2インメモリDBを使用し、`src/test/resources/jdbc.dicon` によりテスト時だけH2接続へ差し替えています。main側の `jdbc.dicon` は変更していません。

S2JDBC / `JdbcManager` を使用して、以下を検証しました。

- DBへの登録
- IDによる検索
- ステータス更新
- 登録件数取得

Excelテストデータは使用せず、Javaコード、Builder、Fixture、SQLファイルでテストデータを準備しています。GitLab CI上でもDBありS2JUnit4テストが成功することを確認しました。

## Excelなしテストデータ準備方式の比較

### Javaコード直接方式

Javaコード上でEntityを直接生成し、必要な値をセットする方式です。

最小検証には向いていますが、テストが増えると同じ初期化コードが重複しやすくなります。

### TestDataBuilder方式

`DbInvoiceTestDataBuilder` で `DbInvoice` オブジェクトを作成する方式です。

`unapprovedInvoice` や `approvedInvoice` など、テストデータの意味をメソッド名で表現できます。AIがテストを生成する際にも意図を読み取りやすく、Entityの項目追加時にも修正範囲を抑えやすい方式です。

### Fixture方式

`DbInvoiceFixture` でDB登録済みの状態を作る方式です。

Builderで作成したデータを `DbInvoiceService` 経由でDBへinsertし、テスト開始時点の業務状態を作りやすくします。

### SQLファイル方式

DDLや固定初期データをSQLファイルで管理する方式です。

Git差分でレビューしやすく、複数TABLEやマスタデータの準備に向いています。

## Builder / Fixture / SQLの使い分け

- Builder: 1件のEntityやDTOを作る
- Fixture: DB登録済みの業務状態を作る
- SQL: DDL、固定マスタ、複数テーブルの基本初期状態を作る

この検証では、Builder / Fixture / SQLファイル方式を置き換え関係ではなく、用途に応じて併用する方針としています。

## 今後の検証課題

- 複数TABLEをまたぐ業務シナリオのFixture化
- 複数Service / 複数Logic / DAOを跨ぐ実業務に近いテスト生成
- LogicCreator追加によるlogicパッケージのS2管理化
- 外部依存やモックが必要な処理のAIテスト生成
- トランザクション・ロールバック確認
- 実業務のExcelテストデータとの比較
- どの粒度までAIにテスト設計を任せられるかの評価
