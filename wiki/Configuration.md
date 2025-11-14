# Configuration（設定）

TNT TAGプラグインの設定ファイルの詳細な説明です。

## 設定ファイル一覧

プラグインは以下の設定ファイルを使用します：

```
plugins/TNT-TAG/
├── config.yml          # メイン設定ファイル
├── arenas.yml          # アリーナ設定
└── messages_ja_JP.yml  # メッセージ設定
```

## config.yml

メインの設定ファイルです。ゲーム全体の動作を制御します。

### 完全な設定例

```yaml
# TNT TAG Configuration
plugin:
  language: ja_JP        # 言語設定（ja_JP, en_US）
  debug: false           # デバッグモード（ログ出力増加）

database:
  type: YAML             # データベースタイプ（YAML, SQLite, MySQL）

game:
  lobby_world: world     # ロビーワールド名
  min_players: 20        # 最小プレイヤー数
  max_players: 25        # 最大プレイヤー数
  rounds: 6              # ラウンド数（固定）
  tag_cooldown: 0.5      # タグ後のクールダウン（秒）
  explosion_countdown: 3 # 爆発前のカウントダウン（秒）
  countdown: 10          # ゲーム開始カウントダウン（秒）
  pvp_enabled: false     # PVPダメージ（常にfalse推奨）

round_settings:
  round_1:
    tnt_holders: 1       # TNT保持者数（固定値）
    duration: 40         # ラウンド時間（秒）
    glowing: false       # 全員発光エフェクト
  round_2:
    tnt_holders_ratio: 0.25  # TNT保持者の割合（25%）
    duration: 30
    glowing: false
  round_3:
    tnt_holders_ratio: 0.25
    duration: 30
    glowing: false
  round_4:
    tnt_holders_ratio: 0.5   # TNT保持者の割合（50%）
    duration: 25
    glowing: false
  round_5:
    tnt_holders_ratio: 0.33  # TNT保持者の割合（33%）
    duration: 40
    glowing: true            # 全員発光
  round_6:
    tnt_holders_ratio: 0.33
    duration: 50
    glowing: true

arena:
  spawn_teleport: true   # ラウンド開始時に中央へテレポート
  use_preset_map: true   # 事前作成マップを使用
  world_border: true     # ワールドボーダーを使用

effects:
  particles: true        # パーティクルエフェクト
  sounds: true           # サウンドエフェクト
  fireworks: true        # 花火エフェクト（勝利時）

rewards:
  enabled: true          # 報酬システム
  win: 100               # 勝利報酬
  per_round_survive: 20  # ラウンド生存報酬
  per_tag: 5             # タグ成功報酬
```

### 設定項目の詳細

#### plugin セクション

| 設定項目 | デフォルト | 説明 |
|---------|----------|------|
| language | ja_JP | 使用する言語（ja_JP, en_US） |
| debug | false | デバッグログを出力（開発用） |

#### database セクション

| 設定項目 | デフォルト | 説明 |
|---------|----------|------|
| type | YAML | データベースタイプ |

**対応データベース**:
- `YAML`: ファイルベース（小規模向け）
- `SQLite`: 軽量DB（中規模向け）
- `MySQL`: 本格DB（大規模向け）

MySQL使用時の追加設定：
```yaml
database:
  type: MySQL
  host: localhost
  port: 3306
  database: tnttag
  username: root
  password: password
```

#### game セクション

| 設定項目 | デフォルト | 説明 |
|---------|----------|------|
| lobby_world | world | ロビーワールドの名前 |
| min_players | 20 | ゲーム開始に必要な最小人数 |
| max_players | 25 | 参加可能な最大人数 |
| rounds | 6 | ラウンド数（変更非推奨） |
| tag_cooldown | 0.5 | タグ後のクールダウン（秒） |
| explosion_countdown | 3 | 爆発前のカウントダウン（秒） |
| countdown | 10 | 開始カウントダウン（秒） |
| pvp_enabled | false | PVPダメージ（常にfalse推奨） |

**調整のヒント**:
- `min_players`: テスト用に2-4に設定可能
- `max_players`: サーバー性能に応じて調整
- `tag_cooldown`: 短すぎると連続タグが可能に

#### round_settings セクション

各ラウンドの設定を個別に調整できます。

**ラウンド設定の構文**:
```yaml
round_X:
  tnt_holders: N              # 固定数（数値指定）
  # または
  tnt_holders_ratio: 0.XX     # 割合（0.0-1.0）
  duration: XX                # ラウンド時間（秒）
  glowing: true/false         # 全員発光
```

**tnt_holders vs tnt_holders_ratio**:
- `tnt_holders`: 固定数（例: 1人）
- `tnt_holders_ratio`: 参加者の割合（例: 0.25 = 25%）

**推奨設定**:
- ラウンド1: 固定1人（序盤は少なめ）
- ラウンド2-3: 25%（緩やかに増加）
- ラウンド4: 50%（半数）
- ラウンド5-6: 33% + 発光（終盤の盛り上がり）

#### arena セクション

| 設定項目 | デフォルト | 説明 |
|---------|----------|------|
| spawn_teleport | true | ラウンド開始時に中央へTP |
| use_preset_map | true | 事前作成マップを使用 |
| world_border | true | ワールドボーダー有効化 |

#### effects セクション

| 設定項目 | デフォルト | 説明 |
|---------|----------|------|
| particles | true | パーティクルエフェクト |
| sounds | true | サウンドエフェクト |
| fireworks | true | 勝利時の花火 |

パフォーマンス向上のため、falseに設定可能です。

#### rewards セクション

| 設定項目 | デフォルト | 説明 |
|---------|----------|------|
| enabled | true | 報酬システム有効化 |
| win | 100 | 勝利時の報酬 |
| per_round_survive | 20 | ラウンド生存報酬 |
| per_tag | 5 | タグ成功報酬 |

経済プラグイン（Vault）連携時に使用されます。

---

## arenas.yml

アリーナの設定を保存します。通常は手動編集不要です。

### 自動生成される内容

```yaml
arenas:
  arena1:
    name: "Classic Arena"
    world: world
    type: MEDIUM
    enabled: true
    center:
      x: 0.0
      y: 64.0
      z: 0.0
    pos1:
      x: -25.0
      y: 54.0
      z: -25.0
    pos2:
      x: 25.0
      y: 74.0
      z: 25.0
    spawn_points:
      - x: 0.0
        y: 64.0
        z: 0.0
        yaw: 0.0
        pitch: 0.0
```

### 手動編集する場合

#### 複数のスポーン地点を設定

```yaml
spawn_points:
  - {x: 10, y: 64, z: 0, yaw: 180, pitch: 0}
  - {x: -10, y: 64, z: 0, yaw: 0, pitch: 0}
  - {x: 0, y: 64, z: 10, yaw: 270, pitch: 0}
  - {x: 0, y: 64, z: -10, yaw: 90, pitch: 0}
```

プレイヤーはランダムにいずれかの地点にスポーンします。

#### アリーナタイプ

| タイプ | サイズ目安 | 推奨人数 |
|-------|----------|---------|
| SMALL | 30x30 | 10-15人 |
| MEDIUM | 50x50 | 20-25人 |
| LARGE | 70x70 | 30-40人 |

---

## messages_ja_JP.yml

ゲーム内メッセージのカスタマイズができます。

### 主要なメッセージ

```yaml
# ゲームメッセージ
game:
  start: "&a&lゲーム開始！TNTから逃げろ！"
  round_start: "&e&lラウンド %round% 開始！"
  round_end: "&6&lラウンド %round% 終了"
  tnt_received: "&c&lTNTを受け取った！他の人にタッチ！"
  tnt_passed: "&a&lTNTを渡した！安全だ！"
  explosion: "&4&l💥 ドカーン！💥"
  eliminated: "&cあなたは爆発しました..."
  victory: "&6&l🏆 勝利！最後の生存者です！"
  spectator: "&7観戦モードになりました"

# エラーメッセージ
errors:
  not_enough_players: "&cプレイヤーが不足しています（最小: %min%人）"
  arena_not_found: "&cアリーナが見つかりません"
  already_in_game: "&c既にゲームに参加しています"
  game_full: "&cゲームが満員です"
  game_in_progress: "&c既にゲーム進行中です"

# コマンドメッセージ
commands:
  join_success: "&a✓ ゲームに参加しました！"
  leave_success: "&a✓ ゲームから退出しました"
  arena_created: "&a✓ アリーナ '%arena%' を作成しました"
  arena_deleted: "&a✓ アリーナ '%arena%' を削除しました"
  config_reloaded: "&a✓ 設定ファイルを再読み込みしました"

# カウントダウンメッセージ
countdown:
  starting: "&eゲーム開始まで %seconds% 秒..."
  round_starting: "&eラウンド開始まで %seconds% 秒..."
```

### カラーコード

Minecraft標準のカラーコードが使用できます：

| コード | 色 | コード | 効果 |
|-------|---|-------|-----|
| &0 | 黒 | &l | 太字 |
| &1 | 濃青 | &m | 取り消し線 |
| &2 | 濃緑 | &n | 下線 |
| &3 | 濃水色 | &o | 斜体 |
| &4 | 濃赤 | &r | リセット |
| &5 | 紫 | |
| &6 | 金色 | |
| &7 | 灰色 | |
| &8 | 濃灰色 | |
| &9 | 青 | |
| &a | 緑 | |
| &b | 水色 | |
| &c | 赤 | |
| &d | ピンク | |
| &e | 黄 | |
| &f | 白 | |

### プレースホルダー

メッセージ内で使用できる変数：

| プレースホルダー | 説明 |
|---------------|------|
| %player% | プレイヤー名 |
| %arena% | アリーナ名 |
| %round% | 現在のラウンド |
| %max_rounds% | 最大ラウンド数 |
| %time% | 残り時間 |
| %survivors% | 生存者数 |
| %holders% | TNT保持者数 |
| %min% | 最小プレイヤー数 |
| %max% | 最大プレイヤー数 |
| %current% | 現在のプレイヤー数 |

**使用例**:
```yaml
scoreboard:
  title: "&c&lTNT TAG"
  lines:
    - "&7ラウンド: &e%round%/%max_rounds%"
    - "&7残り時間: &a%time%"
    - ""
    - "&7生存者: &b%survivors%人"
    - "&7TNT保持者: &c%holders%人"
```

---

## 設定の適用

### 設定変更後の反映

```
/tnttag reload
```

または、サーバーを再起動します。

### 設定のバックアップ

重要な設定変更前にバックアップを推奨：

```bash
cp -r plugins/TNT-TAG plugins/TNT-TAG.backup.$(date +%Y%m%d)
```

### 設定のリセット

デフォルト設定に戻すには：

1. サーバーを停止
2. 設定ファイルを削除
3. サーバーを起動（自動再生成）

```bash
rm plugins/TNT-TAG/config.yml
# サーバー起動で再生成
```

---

## トラブルシューティング

### 設定が反映されない

1. YAMLの構文エラーを確認
   - インデントはスペース2個
   - タブは使用不可
   - コロンの後にスペース必須

2. オンラインYAMLバリデーターで検証
   - https://www.yamllint.com/

3. サーバーログを確認
   ```bash
   grep "TNT-TAG" logs/latest.log | grep ERROR
   ```

### YAMLの基本ルール

```yaml
# 正しい例
game:
  min_players: 20    # コロンの後にスペース
  max_players: 25    # インデントはスペース2個

# 間違った例
game:
  min_players:20     # スペースなし（NG）
    max_players: 25  # インデント不揃い（NG）
```

---

**関連ページ**:
- [Commands](Commands) - コマンドの使い方
- [Arena Setup](Arena-Setup) - アリーナの作成
- [Game Rules](Game-Rules) - ゲームルール詳細
