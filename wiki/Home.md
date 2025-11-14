# TNT TAG Wiki

TNT TAGプラグインの公式ドキュメントへようこそ！

## プラグイン概要

TNT TAGは、Minecraft Paper サーバー向けのサバイバル鬼ごっこ型ミニゲームプラグインです。プレイヤーはTNTの爆発から逃れながら、最後まで生き残ることを目指します。

### 企画・設計
- **クリエイター**: 斉藤ゆうき

### 基本情報
- **プレイ人数**: 20～25人
- **ゲーム時間**: 約5分（全6ラウンド）
- **対応バージョン**: Minecraft 1.21.x (Paper)
- **必須Java**: Java 21以上

## 特徴

### ダイナミックなゲームプレイ
- 6ラウンド制のサバイバルゲーム
- ラウンドごとに変化するTNT保持者数
- スピードブーストによる戦略的な追いかけっこ

### 視覚的エフェクト
- TNT保持者の頭がTNTブロックに変化
- カラフルなパーティクルエフェクト
- 発光エフェクトによる視認性向上
- 爆発時の迫力ある演出

### 充実したUI
- リアルタイムスコアボード
- アクションバーでの状態表示
- タイトルメッセージによる通知
- 詳細なリザルト画面

### カスタマイズ性
- 柔軟な設定ファイル（YAML）
- 複数アリーナ対応
- メッセージのカスタマイズ
- ラウンド設定の調整

## クイックスタート

1. **プラグインのダウンロード**
   - [Releases](../../releases)から最新版をダウンロード

2. **インストール**
   ```
   server/plugins/ フォルダにJARファイルを配置
   サーバーを再起動
   ```

3. **アリーナ作成**
   ```
   /tnttag setpos1  # 角1を設定
   /tnttag setpos2  # 角2を設定
   /tnttag creategame <アリーナ名>  # アリーナ作成
   ```

4. **ゲーム開始**
   ```
   /tnttag join  # プレイヤーが参加
   /tnttag start <アリーナ名>  # 管理者が強制開始（または自動開始）
   ```

詳細は各ページをご覧ください。

## ドキュメント目次

- **[Installation](Installation)** - インストール手順と初期設定
- **[Commands](Commands)** - コマンド一覧と使い方
- **[Configuration](Configuration)** - 設定ファイルの詳細
- **[Game Rules](Game-Rules)** - ゲームルールとラウンド詳細
- **[Arena Setup](Arena-Setup)** - アリーナの作成方法
- **[Troubleshooting](Troubleshooting)** - トラブルシューティングとFAQ

## サポート

問題が発生した場合：

1. **[Troubleshooting](Troubleshooting)ページを確認**
2. **[Issues](../../issues)で既存の問題を検索**
3. **新しいissueを作成**:
   - [Bug Report](../../issues/new?template=bug_report.yml) - バグ報告
   - [Feature Request](../../issues/new?template=feature_request.yml) - 機能提案

## コントリビューション

プロジェクトへの貢献を歓迎します！

- バグ報告
- 機能提案
- ドキュメント改善
- コードコントリビューション

詳細は[CONTRIBUTING.md](../../blob/main/CONTRIBUTING.md)をご覧ください。

## ライセンス

このプロジェクトのライセンスについては[LICENSE](../../blob/main/LICENSE)ファイルをご確認ください。

---

**最終更新**: 2025-11-14
