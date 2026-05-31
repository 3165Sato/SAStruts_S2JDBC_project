# AI生成テスト検証 要約資料

## 1. 検証の目的

この検証では、レガシーJava / Seasar2環境でAI生成テストを実務に活用できるかを確認した。

- Seasar2 / S2JUnit4構成でAI生成テストを使えるか確認する
- S2JUnit4テストをGitLab CIで自動実行できるか確認する
- Excelテストデータ依存を減らせるか確認する
- 複数Service / 複数Logic / DBアクセスを含む実務寄りの処理でもテストできるか確認する

詳細資料:

- `docs/ai-test-generation-evaluation.md`
- `docs/ai-generated-test-review-checklist.md`

## 2. 検証環境

- Framework: Seasar2 / SAStruts / S2JDBC
- Test Framework: S2JUnit4
- Java: プロジェクト指定はJava 1.7、実行はJDK 8
- Build Tool: Maven 3.6.3
- CI: GitLab CI
- DB: H2インメモリDB
- Project: Dolteng生成プロジェクト

## 3. 実施したこと

- 単一Serviceの業務ロジックに対するS2JUnit4テスト生成
- 複数Logic / Storeへの責務分割
- H2 + S2JDBCのDBありテスト
- Builder / Fixture / SQL / Scenario FixtureによるExcelなしテストデータ準備
- 複数TABLEをまたぐ前提データ作成
- 複数Service / 複数Logic / DBアクセスServiceを跨ぐ支払確定処理のテスト
- 意図的バグによるCI失敗確認
- トランザクション・ロールバック検証
- AI生成テストのレビュー観点チェックリスト作成

## 4. 確認できたこと

- S2JUnit4テストはAIで生成可能だった
- GitLab CIでS2JUnit4テストを自動実行できた
- 意図的バグをCIで検知できた
- H2 + S2JDBCのDBありテストも実行できた
- ExcelなしでもBuilder / Fixture / SQLでテストデータを準備できた
- 複数TABLEや複数Serviceを含む実務寄りの構成でもテストできた
- Service層のトランザクション境界により、複数DB更新のロールバックを確認できた

## 5. 分かったこと

- AI生成テストは、明確な業務ルールや状態遷移があると有効
- AIに丸投げするのではなく、人間が仕様・観点・制約を明示する必要がある
- 異常系や副作用検証は明示しないと漏れやすい
- Excel脱却にはBuilder / Fixture / SQLの使い分けが有効
- CIに載せることで、AI生成テストを品質ゲートとして使える可能性がある

## 6. 注意点

- 今回の検証は実業務より単純化している
- 実業務では外部IF、複数DB、排他、バッチ、例外設計などがさらに絡む
- AI生成テストは人間レビューが必須
- Java / Maven / Seasar系依存関係のバージョン固定が重要
- Maven 3.9系ではHTTPリポジトリブロックに注意が必要

## 7. 業務適用するなら

- まずは単体のService / LogicテストからAI生成を試す
- 次にDBありS2JUnit4テストへ広げる
- Excelテストデータは一括移行ではなく、Builder / Fixture / SQLへ段階的に移行する
- AI生成テストのレビュー観点チェックリストを使う
- CIで必ず自動実行する
- 重要業務ロジックでは意図的バグを入れて検知力を確認する

## 8. 次の検証候補

- 外部IFやモックを含む処理
- バッチ処理
- 排他制御
- 複数DB更新
- 実業務ExcelデータからBuilder / Fixtureへの移行試験
- AI生成テストのレビュー基準のチーム展開

## 9. まとめ

レガシーなSeasar2 / S2JUnit4環境でも、AI生成テストとCI/CDの組み合わせには十分な検証価値がある。

また、Excelテストデータ依存を減らす代替案として、Builder / Fixture / SQL / Scenario Fixtureの組み合わせも現実的である。

ただし、AI任せではなく、人間が仕様・観点・制約を整理したうえで、AIを実装補助として活用する進め方が現実的である。
