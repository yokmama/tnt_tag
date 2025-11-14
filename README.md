# TNT TAG

TNT TAGは、Minecraft上で動作するサバイバル鬼ごっこ型ミニゲームです。TNTを持ったプレイヤー（鬼）が他のプレイヤーにTNTを押し付け、爆発までに逃げ切ることを目指します。

## プロジェクト概要

- **ゲーム名**: TNT TAG（TNTタグ）
- **ジャンル**: サバイバル鬼ごっこ型ミニゲーム
- **プレイ人数**: 20～25人推奨（最小4人から開始可能）
- **ゲーム時間**: 全6ラウンド（約5分）
- **企画・設計**: 斉藤ゆうき

### ゲームの目的

TNTの爆発に巻き込まれずに最後まで生き残ること。各ラウンドでランダムに選ばれた「鬼」がTNTを持ち、他のプレイヤーに押し付けます。時間切れ時にTNTを持っているプレイヤーのみが爆発して脱落します。

### 主な特徴

- **6ラウンド制**: 各ラウンドで異なる難易度とTNT保持者数
- **スピード効果**: TNT保持者は移動速度が上昇
- **視覚効果**: 豊富なパーティクル、サウンド、HUD表示
- **複数アリーナ対応**: 事前に設定したアリーナでプレイ可能
- **統計システム**: プレイヤーの戦績を記録

## 環境構築

### 必要な環境

- **Java**: Java 21以上
- **Kotlin**: 2.2.21
- **Minecraft Server**: Paper 1.21.10以上
- **ビルドツール**: Gradle 8.x

### 開発環境のセットアップ

1. **リポジトリのクローン**

```bash
git clone <repository-url>
cd tnt_tag
```

2. **Gradleビルドの確認**

```bash
./gradlew build
```

3. **プラグインのビルド**

```bash
./gradlew shadowJar
```

ビルド成功後、`build/libs/TNTTag-1.0.0-all.jar` が生成されます。

### クイックスタート（開発・テスト用）

**最も簡単な方法**: 以下のコマンド1つでサーバーを起動できます。

```bash
./gradlew runServer
```

このコマンドは以下を自動的に実行します:
- プラグインのビルド
- Paper サーバーのダウンロード（初回のみ）
- プラグインのインストール
- サーバーの起動

初回起動時は、`eula.txt` に同意する必要があります:

```bash
# サーバーが停止したら、eulaに同意
echo "eula=true" > run/eula.txt

# 再度サーバーを起動
./gradlew runServer
```

サーバーが起動したら、Minecraftクライアントから `localhost` に接続してプレイできます。

### サーバーへのインストール（本番環境）

既存のMinecraftサーバーにプラグインをインストールする場合:

1. **プラグインファイルの配置**

```bash
# ビルドしたJARファイルをサーバーのpluginsディレクトリにコピー
cp build/libs/TNTTag-1.0.0-all.jar /path/to/minecraft/server/plugins/
```

2. **サーバーの起動**

```bash
cd /path/to/minecraft/server
java -Xmx2G -Xms2G -jar paper-1.21.10.jar nogui
```

3. **プラグインの確認**

サーバー起動後、コンソールまたはゲーム内で以下のコマンドを実行:

```
/plugins
```

`TNTTag` が緑色で表示されていれば正常にロードされています。

## ゲームのセットアップ

### 1. アリーナの作成

アリーナは事前に用意されたマップ領域です。

```bash
# ゲーム内でアリーナ領域の最初の角を設定
/tnttag setpos1

# 対角の角を設定
/tnttag setpos2

# アリーナを作成（例: arena1という名前で作成）
/tnttag creategame arena1
```

#### アリーナの推奨仕様

- **サイズ**: 50x50ブロック程度（20-25人用）
- **高さ**: プレイヤーが移動できる十分な高さ
- **障害物**: 隠れ場所や逃走ルートとなる構造物
- **中央スポーン**: プレイヤーが集まる開けた場所

### 2. 設定ファイルの確認

初回起動後、`plugins/TNTTag/` ディレクトリに設定ファイルが生成されます。

```
plugins/TNTTag/
├── config.yml          # メイン設定
├── arenas.yml          # アリーナ情報
├── messages_ja_JP.yml  # メッセージ設定
└── stats/              # プレイヤー統計
```

#### config.yml の主要設定

```yaml
game:
  min_players: 4              # 最小プレイヤー数
  max_players: 25             # 最大プレイヤー数
  rounds: 6                   # ラウンド数
  tag_cooldown: 0.5           # タグクールダウン（秒）
  explosion_countdown: 3      # 爆発カウントダウン（秒）
```

設定変更後は `/tnttag reload` でリロードできます。

## ゲームの開始方法

### 基本的な流れ

**ゲームの開始** (管理者):

```bash
/tnttag start <arena名>
```

例:
```bash
/tnttag start arena1
```

アリーナが設定されている場合、このコマンドでゲームが開始されます。

### ゲームの流れ

1. **ゲーム開始**: 管理者が `/tnttag start <arena名>` を実行
2. **ラウンド1-6**: 各ラウンドでTNTの押し付け合い
   - ラウンド開始時に全員中央にテレポート
   - TNT保持者がランダムに選出
   - TNT保持者は他のプレイヤーを殴ってTNTを渡す
   - 時間切れでTNT保持者が爆発
3. **結果表示**: 最終順位と統計を表示

## コマンド一覧

### プレイヤーコマンド

| コマンド | 説明 | 権限 |
|---------|------|------|
| `/tnttag list` | アリーナ一覧 | なし |
| `/tnttag stats [player]` | 統計を表示 | なし |

### 管理者コマンド

| コマンド | 説明 | 権限 |
|---------|------|------|
| `/tnttag setpos1` | アリーナ領域の最初の角を設定 | OP |
| `/tnttag setpos2` | アリーナ領域の対角を設定 | OP |
| `/tnttag creategame <arena>` | アリーナを作成 | OP |
| `/tnttag delete <arena>` | アリーナを削除 | OP |
| `/tnttag start <arena>` | ゲーム強制開始 | OP |
| `/tnttag stop <arena>` | ゲーム強制停止 | OP |
| `/tnttag reload` | 設定リロード | OP |

## ラウンド詳細

| ラウンド | TNT保持者数 | 爆発時間 | 特殊効果 |
|---------|------------|---------|---------|
| 1 | 1人 | 40秒 | なし |
| 2 | 全体の1/4 | 30秒 | なし |
| 3 | 全体の1/4 | 30秒 | なし |
| 4 | 全体の1/2 | 25秒 | なし |
| 5 | 全体の1/3 | 40秒 | 全員発光 |
| 6 | 全体の1/3 | 50秒 | 全員発光 |

## トラブルシューティング

### プラグインが読み込まれない

- Java 21以上がインストールされているか確認
- Paper 1.21.10以上のサーバーを使用しているか確認
- サーバーログでエラーメッセージを確認

### ゲームが開始しない

- 最小プレイヤー数（デフォルト4人）が揃っているか確認
- アリーナが正しく設定されているか確認: `/tnttag list`
- config.ymlの `min_players` 設定を確認

### アリーナが見つからない

```bash
# アリーナ一覧を確認
/tnttag list

# アリーナを再作成
/tnttag setpos1
/tnttag setpos2
/tnttag creategame <arena-name>
```

### 設定が反映されない

```bash
# 設定をリロード
/tnttag reload
```

## クイックスタートガイド

ゲームを始めるための最短手順:

```bash
# 1. サーバーを起動（初回）
./gradlew runServer

# 2. EULA同意（初回のみ）
echo "eula=true" > run/eula.txt

# 3. サーバー再起動
./gradlew runServer
```

サーバー起動後、Minecraftクライアントから `localhost` に接続し:

```bash
# 4. アリーナを作成（OP権限が必要）
/op <あなたの名前>
/tnttag setpos1      # 角1を設定
/tnttag setpos2      # 角2を設定
/tnttag creategame arena1

# 5. ゲームを開始
/tnttag start arena1
```

## ライセンス

このプロジェクトはMITライセンスの下で公開されています。

## クレジット

- **企画・設計**: 斉藤ゆうき

## サポート

バグ報告や機能要望は、GitHubのIssuesセクションにお願いします。

---

**楽しいTNT TAGライフを！**
