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
