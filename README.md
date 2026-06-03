# sastruts_s2jdbc_project

## 概要

このプロジェクトは、Doltengで生成した Seasar2 / SAStruts / S2JDBC 構成のサンプルアプリケーションです。

目的は、レガシーJavaフレームワーク環境に対して、AIによる実装追加・テストコード生成・CI実行を検証することです。

## 外部公開向けの注意

このリポジトリは、個人の技術検証用サンプルです。

- 実システム、実データ、実認証情報は含みません
- DB接続情報はローカル検証用のサンプルです
- 古いJava / Seasar2 / S2JUnit4構成を意図的に使用しています
- 本番利用を目的とした構成ではありません
- 外部公開に向けて、実認証情報と誤解される可能性がある接続例やH2物理DBファイルは除外しています

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

このリポジトリは、レガシーJava環境におけるAI活用、S2JUnit4テストのCI実行、CI/CD実現性、Excel形式のテストデータに依存しない方式を検証するためのサンプル環境です。

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
| Level 8 | 複数TABLEをまたぐScenario Fixture検証 | 完了 |
| Level 9 | 複数Service / 複数Logic / DBアクセスServiceを跨ぐ業務シナリオ検証 | 完了 |

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

## 複数TABLE Scenario Fixture検証

単一テーブルだけでなく、複数TABLEをまたぐ前提データをExcelなしで作成できることを確認しました。

この検証では、顧客、部署、請求書、承認履歴などの複数テーブルを組み合わせた業務シナリオをScenario Fixtureで作成しました。例えば「承認済み請求書シナリオ」では、顧客・部署・請求書・承認履歴をまとめて作成できます。

Builder / Fixture / SQLファイルを組み合わせることで、複数TABLEのテストデータ準備が可能であることを確認しました。Scenario Fixtureは、複数テーブルにまたがる「業務状態」を作る役割です。

このScenario Fixture検証も、ローカルおよびGitLab CIで成功することを確認済みです。

## 複数Service / 複数Logic / DBアクセスServiceを跨ぐ業務シナリオ検証

実業務に近い構成として、請求書支払確定処理を追加し、複数Service / 複数Logic / DBアクセスServiceを跨ぐS2JUnit4テストを検証しました。

この検証では、`InvoicePaymentConfirmService` を業務処理の入口とし、以下の呼び出し関係で処理を組み立てています。

- `InvoicePaymentConfirmService`: 支払確定処理全体の入口
- `InvoicePaymentConfirmValidationLogic`: 支払確定可否の判定
- `InvoicePaymentHistoryLogic`: 支払確定履歴Entityの作成
- `DbScenarioInvoiceService`: 請求書の取得とステータス更新
- `DbApprovalHistoryService`: 履歴の登録・検索

S2JUnit4テストでは、以下を確認しました。

- 承認済み請求書を支払確定できる
- 支払確定後に請求書ステータスが `PAYMENT_CONFIRMED` になる
- 支払確定履歴が登録される
- 支払確定履歴の `INVOICE_ID` が対象請求書IDと一致する
- 支払確定履歴の `STATUS` が `PAYMENT_CONFIRMED` である
- 未承認・差戻し済み・支払確定済み・存在しないIDではエラーになる
- 異常時に請求書ステータスや履歴件数が変わらない

この検証により、AI生成テストでも複数クラスを跨ぐ業務処理を検証できることを確認しました。一方で、副作用検証は明示的に指示しないと漏れやすいため、異常時のDB状態不変性や履歴二重登録防止は人間が観点として明示する必要があります。

また、Scenario Fixtureにより複数TABLEの前提データを準備しやすくなり、承認済み請求書、差戻し済み請求書などの業務状態をテスト開始時点で表現できることも確認しました。

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

## Builder / Fixture / Scenario Fixture / SQLの使い分け

- Builder: 1件のEntityやDTOを作る
- Fixture: 1テーブルまたは1機能単位で、DB登録済み状態を作る
- Scenario Fixture: 複数TABLEをまたぐ業務シナリオ状態を作る
- SQL: DDL、固定マスタ、複数テーブルの基本初期状態を作る

この検証では、Builder / Fixture / Scenario Fixture / SQLファイル方式を置き換え関係ではなく、用途に応じて併用する方針としています。

## テストパッケージ構成

テストクラスは、検証対象の責務に合わせて以下のように整理しています。

- `org.seasar.sastruts.example.logic`: 入力検証、状態遷移、金額変更など、DB副作用を伴わないLogic層単体テスト
- `org.seasar.sastruts.example.service`: 業務Service、DBアクセスService、Scenario Fixture、トランザクションなど、Service/Application層テスト
- `org.seasar.sastruts.example.testsupport`: Builder、Fixture、Scenario Fixture、SQL実行補助などのテスト補助クラス

これにより、業務ルール単体の検証と、複数クラス・複数TABLEを跨ぐ業務フロー検証を分けて確認しやすくしています。

## トランザクション・ロールバック検証

`InvoicePaymentConfirmService#confirmPayment(Long invoiceId)` を対象に、複数DB更新を伴う業務Serviceで途中失敗時にロールバックされることを検証しました。

支払確定処理では、`DB_SCENARIO_INVOICE` の請求書ステータスを `PAYMENT_CONFIRMED` に更新し、続けて `DB_APPROVAL_HISTORY` に支払確定履歴を登録します。今回のテストでは、承認済み請求書を前提に、履歴登録時に `RuntimeException` を発生させました。

本番コードには失敗注入用のフラグや分岐を追加せず、S2JUnit4テスト側で `DbApprovalHistoryService` の失敗版へ一時的に差し替えることで、テスト専用の失敗を注入しています。

例外発生後、以下を確認しました。

- 請求書ステータスが `PAYMENT_CONFIRMED` にならず、`APPROVED` のままであること
- 支払確定履歴の件数が増えていないこと
- ローカルの `mvn clean test` が成功すること
- GitLab CIでも成功すること

この結果により、Service層に設定されたトランザクション境界により、複数DB更新がまとめてロールバックされることを確認できました。

### トランザクション境界の方針

このプロジェクトでは、業務処理の入口であるService層にトランザクション境界を置く方針としています。

- Service層: 業務入口、処理フロー制御、DBアクセスServiceの呼び出しを担当し、トランザクション境界を持つ
- Logic層: 入力検証、状態遷移判定、履歴Entity生成などを担当し、トランザクション境界は持たない
- DBアクセスService: S2JDBC / `JdbcManager` による登録、検索、更新を担当し、業務Serviceから呼ばれる場合は外側のトランザクションに参加する

`logicCustomizer` にはトランザクション系customizerを付けず、`traceCustomizer` のみにしています。これにより、「どこがトランザクション境界か」を明確にしたまま検証を進められます。

## 今後の検証課題

- LogicCreator追加によるlogicパッケージのS2管理化
- 外部依存やモックが必要な処理のAIテスト生成
- トランザクション・ロールバック確認
- トランザクション途中失敗時のロールバック検証
- より複雑な途中失敗パターンのロールバック検証
- 外部連携失敗時のロールバック検証
- 外部依存やモックを含むテスト
- より実業務に近い複数機能連携
- 実業務のExcelテストデータとの比較
- Scenario Fixtureが複雑化した場合の保守性確認
- 実業務のExcelテストデータをScenario Fixtureへ移行する場合の粒度設計
- どの粒度までAIにテスト設計を任せられるかの評価
