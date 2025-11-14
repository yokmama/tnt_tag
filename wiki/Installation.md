# Installation（インストール）

TNT TAGプラグインのインストール手順を説明します。

## 必要な環境

### サーバー要件
- **Minecraftバージョン**: 1.21.x
- **サーバータイプ**: Paper 1.21.10 以上
  - Spigot、CraftBukkitでは動作しません
  - Paperの最新版を推奨します
- **Java**: Java 21以上
- **メモリ**: 最低2GB（推奨4GB以上）

### 推奨プレイヤー数
- 最小: 20人
- 最大: 25人
- テスト用最小: 4人（config.ymlで変更可能）

### 依存プラグイン
このプラグインは単独で動作します。追加の依存プラグインは不要です。

## インストール手順

### 1. Paperサーバーの準備

Paperサーバーをまだインストールしていない場合：

```bash
# Paperサーバーのダウンロード
wget https://api.papermc.io/v2/projects/paper/versions/1.21.10/builds/XX/downloads/paper-1.21.10-XX.jar

# サーバーの起動（初回）
java -Xmx4G -Xms2G -jar paper-1.21.10-XX.jar --nogui

# eula.txtを編集してeula=trueに変更
echo "eula=true" > eula.txt

# サーバーを再起動
java -Xmx4G -Xms2G -jar paper-1.21.10-XX.jar --nogui
```

### 2. TNT TAGプラグインのダウンロード

最新版のプラグインをダウンロードします：

1. [Releases](../../releases)ページにアクセス
2. 最新バージョンの`TNT-TAG-x.x.x.jar`をダウンロード

または、ソースからビルドする場合：

```bash
# リポジトリをクローン
git clone https://github.com/YOUR_USERNAME/tnt_tag.git
cd tnt_tag

# Gradleでビルド
./gradlew build

# ビルドされたJARファイルは build/libs/ に生成されます
```

### 3. プラグインのインストール

1. ダウンロードした`TNT-TAG-x.x.x.jar`を`server/plugins/`フォルダにコピー

```bash
cp TNT-TAG-x.x.x.jar /path/to/server/plugins/
```

2. サーバーを再起動

```bash
# サーバーコンソールで
stop

# または Ctrl+C で停止後、再起動
java -Xmx4G -Xms2G -jar paper-1.21.10-XX.jar --nogui
```

### 4. インストール確認

サーバーコンソールで以下を確認：

```
[HH:MM:SS INFO]: [TNT-TAG] Enabling TNT-TAG vX.X.X
[HH:MM:SS INFO]: [TNT-TAG] TNT TAG has been enabled!
```

ゲーム内で確認：

```
/plugins
```

緑色で`TNT-TAG`が表示されればインストール成功です。

## 初期設定

### 1. 設定ファイルの生成

初回起動時、`plugins/TNT-TAG/`フォルダに以下のファイルが自動生成されます：

```
plugins/TNT-TAG/
├── config.yml          # メイン設定ファイル
├── arenas.yml          # アリーナ設定
└── messages_ja_JP.yml  # メッセージ設定（日本語）
```

### 2. 基本設定の確認

`config.yml`を開き、基本設定を確認します：

```yaml
plugin:
  language: ja_JP        # 言語設定
  debug: false           # デバッグモード

game:
  min_players: 20        # 最小プレイヤー数
  max_players: 25        # 最大プレイヤー数
  countdown: 10          # 開始カウントダウン（秒）
```

テスト用に最小プレイヤー数を変更する場合：

```yaml
game:
  min_players: 2  # テスト用
```

### 3. ワールドの準備

専用のゲームワールドを作成することを推奨します：

1. サーバーを停止
2. 新しいワールドフォルダを作成（例：`tnt_world`）
3. `server.properties`または`bukkit.yml`でワールドを設定
4. サーバーを再起動

または、既存のワールドを使用することも可能です。

### 4. 設定の再読み込み

設定を変更した場合、サーバーを再起動せずに反映できます：

```
/tnttag reload
```

## 次のステップ

インストールが完了したら：

1. **[Arena Setup](Arena-Setup)** - アリーナを作成
2. **[Configuration](Configuration)** - 詳細設定をカスタマイズ
3. **[Commands](Commands)** - コマンドの使い方を確認

## トラブルシューティング

### プラグインが読み込まれない

**症状**: `/plugins`でTNT-TAGが赤色表示、またはリストに表示されない

**原因と解決策**:
1. **Javaバージョンが古い**
   ```bash
   java -version
   # Java 21以上が必要
   ```

2. **Paperサーバーではない**
   - SpigotやCraftBukkitでは動作しません
   - Paperサーバーを使用してください

3. **JARファイルが破損している**
   - 再ダウンロードしてください

4. **依存関係の問題**
   - サーバーログ（`logs/latest.log`）を確認

### サーバーログの確認

```bash
# 最新のログを確認
tail -f logs/latest.log

# エラーを検索
grep ERROR logs/latest.log | grep TNT-TAG
```

### コンソールエラーが出る

サーバーコンソールに赤いエラーメッセージが表示される場合：

1. エラーメッセージ全体をコピー
2. [Issues](../../issues)で同じエラーを検索
3. 見つからない場合、[Bug Report](../../issues/new?template=bug_report.yml)を作成

### パフォーマンスの問題

25人でプレイ時にラグが発生する場合：

1. **サーバーメモリを増やす**
   ```bash
   java -Xmx6G -Xms4G -jar paper-1.21.10-XX.jar --nogui
   ```

2. **Paper最適化設定を確認**
   - `paper-global.yml`と`paper-world-defaults.yml`を調整

3. **不要なプラグインを削除**
   - 使用していないプラグインを無効化

## アップデート方法

新しいバージョンがリリースされた場合：

1. サーバーを停止
2. 古いJARファイルを削除または移動
3. 新しいJARファイルを`plugins/`に配置
4. サーバーを起動

設定ファイルは自動的に保持されます。

### 設定ファイルのバックアップ

アップデート前に設定をバックアップすることを推奨：

```bash
cp -r plugins/TNT-TAG plugins/TNT-TAG.backup
```

---

**次のページ**: [Commands](Commands) - コマンドの使い方
