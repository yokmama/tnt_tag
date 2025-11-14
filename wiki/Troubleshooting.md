# Troubleshooting（トラブルシューティング）

TNT TAGプラグインでよくある問題と解決方法、FAQをまとめています。

## 目次

- [インストール・起動の問題](#インストール起動の問題)
- [ゲームプレイの問題](#ゲームプレイの問題)
- [パフォーマンスの問題](#パフォーマンスの問題)
- [コマンドの問題](#コマンドの問題)
- [設定ファイルの問題](#設定ファイルの問題)
- [FAQ](#faq)

---

## インストール・起動の問題

### プラグインが読み込まれない

**症状**:
- `/plugins` でTNT-TAGが赤色表示
- リストに表示されない
- サーバーログにエラー

#### 原因1: Javaバージョンが古い

**確認方法**:
```bash
java -version
```

**必要バージョン**: Java 21以上

**解決策**:
```bash
# Java 21のインストール（Ubuntu/Debian）
sudo apt update
sudo apt install openjdk-21-jdk

# Java 21のインストール（CentOS/RHEL）
sudo yum install java-21-openjdk

# バージョン切り替え
sudo update-alternatives --config java
```

#### 原因2: Paperサーバーではない

**確認方法**:
```
サーバーコンソールの起動ログを確認
"Loading libraries, please wait..." の後に "Paper" の文字
```

**解決策**:
- SpigotやCraftBukkitでは動作しません
- Paperサーバーをダウンロード: https://papermc.io/downloads

#### 原因3: JARファイルが破損している

**確認方法**:
```bash
# ファイルサイズを確認
ls -lh plugins/TNT-TAG-*.jar
```

**解決策**:
1. JARファイルを再ダウンロード
2. ダウンロード時にエラーがないか確認
3. ブラウザのキャッシュをクリア

#### 原因4: 依存関係の問題

**確認方法**:
```bash
# ログを確認
grep "TNT-TAG" logs/latest.log
```

**解決策**:
サーバーログのエラーメッセージを確認し、不足しているライブラリをインストール

---

### サーバーが起動時にクラッシュする

**症状**:
- TNT-TAG有効化後にサーバーがクラッシュ
- OutOfMemoryError

#### 原因: メモリ不足

**確認方法**:
```bash
# ログで確認
grep "OutOfMemoryError" logs/latest.log
```

**解決策**:
```bash
# メモリを増やしてサーバー起動
java -Xmx4G -Xms2G -jar paper.jar --nogui

# 推奨設定（25人プレイ用）
java -Xmx6G -Xms4G -jar paper.jar --nogui
```

---

### 設定ファイルが生成されない

**症状**:
- `plugins/TNT-TAG/` フォルダが空
- config.yml が存在しない

**原因**: プラグインが正常に有効化されていない

**解決策**:
1. サーバーを完全に停止
2. `plugins/TNT-TAG/` フォルダを削除
3. サーバーを再起動
4. 自動生成を確認

```bash
rm -rf plugins/TNT-TAG/
# サーバー再起動
```

---

## ゲームプレイの問題

### ゲームが開始されない

#### 問題1: プレイヤーが不足している

**症状**: 「プレイヤーが不足しています」のメッセージ

**解決策**:
```yaml
# config.yml を編集（テスト用）
game:
  min_players: 2  # デフォルト: 20
```

#### 問題2: アリーナが見つからない

**症状**: 「アリーナが見つかりません」のエラー

**解決策**:
```bash
# アリーナ一覧を確認
/tnttag list

# アリーナが存在しない場合は作成
/tnttag setpos1
/tnttag setpos2
/tnttag creategame arena1
```

#### 問題3: アリーナが「準備中」のまま

**症状**: アリーナのステータスが変わらない

**解決策**:
```bash
# ゲームを強制終了
/tnttag stop <アリーナ名>

# サーバー再起動
stop
```

---

### TNTのタグができない

**症状**:
- 殴ってもTNTが渡らない
- タグが反応しない

#### 原因1: 距離が遠い

**解決策**: 3ブロック以内に近づく

#### 原因2: クールダウン中

**解決策**: 0.5秒待ってから再度タグ

#### 原因3: TNT保持者ではない

**確認**: TNT保持者のみが非保持者にタグできます

#### 原因4: PVPが無効になっている（ワールド設定）

**確認方法**:
```yaml
# bukkit.yml または paper-world.yml
world-settings:
  default:
    pvp: true  # これがtrueであること
```

**解決策**:
```bash
# ワールド設定を確認
/gamerule pvp true
```

---

### プレイヤーがスポーン地点にテレポートされない

**症状**: ラウンド開始時に移動しない

**原因**: アリーナ設定の問題

**解決策**:
```yaml
# arenas.yml を確認
arenas:
  arena1:
    spawn_points:
      - x: 0
        y: 64
        z: 0
        yaw: 0
        pitch: 0
```

座標が正しいか確認し、`/tnttag reload` で反映。

---

### 爆発してもプレイヤーが脱落しない

**症状**: 爆発後もゲームに残る

**原因**: イベントハンドラーの問題

**解決策**:
1. サーバーログを確認
2. 他のプラグインとの競合をチェック
3. TNT-TAGのみで動作確認

```bash
# デバッグモードを有効化
# config.yml
plugin:
  debug: true

# ログを確認
tail -f logs/latest.log | grep TNT-TAG
```

---

## パフォーマンスの問題

### ラグが発生する

**症状**:
- TPS低下
- プレイヤーの移動がカクつく
- エフェクトの遅延

#### 原因1: サーバースペック不足

**確認方法**:
```bash
# TPSを確認
/tps

# 理想: 20.0 TPS
# 許容: 18.0 TPS以上
# 問題: 15.0 TPS以下
```

**解決策**:
1. メモリを増やす
2. 他のプラグインを減らす
3. サーバーをアップグレード

#### 原因2: パーティクルエフェクトが重い

**解決策**:
```yaml
# config.yml
effects:
  particles: false  # パーティクルを無効化
  sounds: true      # サウンドは維持
```

#### 原因3: 同時に複数ゲームが進行

**解決策**:
- アリーナ数を制限
- プレイヤー数を調整
- ゲーム時間を短縮

---

### メモリリーク

**症状**:
- 長時間稼働後にメモリ使用量が増加
- サーバーが徐々に重くなる

**解決策**:
1. 定期的にサーバー再起動
2. 最新版のプラグインを使用
3. Issueで報告

```bash
# 自動再起動スクリプト（cron）
0 4 * * * /path/to/restart_script.sh
```

---

## コマンドの問題

### コマンドが実行できない

**症状**: 「権限がありません」のエラー

#### 原因1: 権限が不足

**解決策**:
```bash
# 管理者権限を付与
/op <プレイヤー名>

# または LuckPerms を使用
/lp user <プレイヤー名> permission set tnttag.admin true
```

#### 原因2: コマンド名が間違っている

**確認**: `/tnttag` または `/tnt`（エイリアス）

#### 原因3: プラグインが無効

**確認**:
```bash
/plugins

# TNT-TAG が緑色で表示されるか確認
```

---

### タブ補完が機能しない

**症状**: Tabキーで候補が表示されない

**原因**: サーバーの問題またはプラグインの不具合

**解決策**:
1. サーバーを再起動
2. 他のプラグインを無効化してテスト
3. Paper最新版を使用

---

## 設定ファイルの問題

### 設定が反映されない

**症状**: config.yml を変更しても反映されない

#### 原因1: リロードしていない

**解決策**:
```bash
/tnttag reload
```

#### 原因2: YAML構文エラー

**確認方法**:
```bash
# ログでエラーを確認
grep "YAML" logs/latest.log
```

**解決策**:
1. オンラインYAMLバリデーターで確認: https://www.yamllint.com/
2. インデントをスペース2個に統一
3. タブ文字を使用しない

#### 原因3: 設定項目名が間違っている

**確認**: デフォルトのconfig.ymlと比較

**解決策**:
```bash
# バックアップから復元
cp plugins/TNT-TAG.backup/config.yml plugins/TNT-TAG/

# または再生成
rm plugins/TNT-TAG/config.yml
# サーバー再起動
```

---

### YAMLの構文エラー

**よくあるミス**:

#### ❌ 間違い1: タブを使用
```yaml
game:
	min_players: 20  # タブはNG
```

#### ✅ 正しい1: スペースを使用
```yaml
game:
  min_players: 20  # スペース2個
```

#### ❌ 間違い2: コロンの後にスペースなし
```yaml
game:
  min_players:20  # スペースなし
```

#### ✅ 正しい2: コロンの後にスペース
```yaml
game:
  min_players: 20  # スペースあり
```

#### ❌ 間違い3: インデント不揃い
```yaml
game:
  min_players: 20
    max_players: 25  # インデントが深すぎる
```

#### ✅ 正しい3: インデント統一
```yaml
game:
  min_players: 20
  max_players: 25  # 揃っている
```

---

## FAQ

### ゲーム全般

#### Q: 途中参加はできますか？
**A**: いいえ、ゲーム開始前のみ参加可能です。

#### Q: 途中退出するとペナルティはありますか？
**A**: 脱落扱いになりますが、ペナルティはありません。

#### Q: 観戦モードになった後、復帰できますか？
**A**: いいえ、そのゲームでは復帰できません。次のゲームに参加してください。

#### Q: 複数のアリーナで同時にゲームできますか？
**A**: はい、可能です。各アリーナで独立してゲームが進行します。

---

### 設定・カスタマイズ

#### Q: ラウンド数を変更できますか？
**A**: config.ymlの`rounds`を変更できますが、6ラウンド推奨です。

#### Q: TNT保持者の移動速度を変更できますか？
**A**: 現在は固定ですが、将来のバージョンで対応予定です。

#### Q: 報酬システムを無効化できますか？
**A**: はい、config.ymlで`rewards.enabled: false`に設定してください。

#### Q: メッセージを英語に変更できますか？
**A**: はい、`language: en_US`に設定してください（英語ファイルがある場合）。

---

### 技術的な質問

#### Q: データベースはどれを選ぶべきですか？
**A**:
- 小規模（50人以下）: YAML
- 中規模（50-200人）: SQLite
- 大規模（200人以上）: MySQL

#### Q: 他のプラグインと競合しますか？
**A**: 通常は競合しませんが、以下のような場合は注意：
- ワールド管理プラグイン（Multiverse等）
- 戦闘系プラグイン（PVP制御系）
- カスタムエフェクトプラグイン

#### Q: バックアップは必要ですか？
**A**: はい、定期的なバックアップを推奨します：
```bash
cp -r plugins/TNT-TAG plugins/TNT-TAG.backup.$(date +%Y%m%d)
```

#### Q: アップデート時にデータは保持されますか？
**A**: はい、設定ファイルと統計データは保持されます。

---

### トラブル対応

#### Q: プレイヤーが壁にハマった場合は？
**A**:
```bash
# プレイヤーをスポーン地点にテレポート
/tp <プレイヤー名> <スポーン座標>

# またはゲームを再起動
/tnttag stop <アリーナ名>
/tnttag start <アリーナ名>
```

#### Q: ゲームが終わらない場合は？
**A**:
```bash
/tnttag stop <アリーナ名>
```

#### Q: 統計データをリセットしたい場合は？
**A**:
```bash
# サーバー停止
# データファイルを削除（YAMLの場合）
rm plugins/TNT-TAG/playerdata/*.yml

# または全削除
rm -rf plugins/TNT-TAG/playerdata/
```

---

## ログの確認方法

### リアルタイムでログを確認

```bash
# 最新のログをリアルタイム表示
tail -f logs/latest.log

# TNT-TAG関連のみ
tail -f logs/latest.log | grep TNT-TAG
```

### エラーログの検索

```bash
# エラーを検索
grep ERROR logs/latest.log | grep TNT-TAG

# 警告を検索
grep WARN logs/latest.log | grep TNT-TAG

# 特定の日付のログ
grep "2025-11-14" logs/latest.log | grep TNT-TAG
```

### デバッグモードの有効化

```yaml
# config.yml
plugin:
  debug: true
```

再読み込み:
```bash
/tnttag reload
```

---

## サポートが必要な場合

### 1. 情報を収集

以下の情報を準備してください：

- TNT-TAGのバージョン
- Paperのバージョン
- Javaのバージョン
- エラーメッセージ（あれば）
- 関連するログ
- 再現手順

### 2. 既存のIssueを確認

[Issues](../../issues)で同じ問題が報告されていないか確認。

### 3. 新しいIssueを作成

- [Bug Report](../../issues/new?template=bug_report.yml) - バグ報告
- [Feature Request](../../issues/new?template=feature_request.yml) - 機能提案

### 4. 情報を提供

できるだけ詳細な情報を提供してください：
- 再現可能な手順
- スクリーンショット
- ログファイル
- 設定ファイル

---

## 緊急時の対応

### プラグインが原因でサーバーが起動しない

```bash
# 1. サーバー停止
# 2. プラグインを一時的に無効化
mv plugins/TNT-TAG-*.jar plugins/TNT-TAG-*.jar.disabled

# 3. サーバー起動
# 4. 問題を報告
```

### データを完全にリセット

```bash
# サーバー停止
# プラグインフォルダを削除
rm -rf plugins/TNT-TAG/

# サーバー起動（クリーンインストール）
```

---

**関連ページ**:
- [Installation](Installation) - インストール手順
- [Configuration](Configuration) - 設定の詳細
- [Commands](Commands) - コマンド一覧
