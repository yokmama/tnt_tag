# TNT TAG ゲーム仕様書

## クリエイター
- **企画・設計**: 斉藤ゆうき

## ゲーム概要
- **ゲーム名**: TNT TAG（TNTタグ）
- **ジャンル**: サバイバル鬼ごっこ型ミニゲーム
- **プレイ人数**: 20～25人
- **ゲーム時間**: 全6ラウンド（約5分）
- **目的**: TNTの爆発に巻き込まれずに最後まで生き残ること
- **概要**: 各ラウンドでランダムに選ばれた「鬼」がTNTを持ち、他のプレイヤーに押し付けます。時間切れ時にTNTを持っているプレイヤーのみが爆発して脱落します。

## ゲーム設定

### 基本ルール
1. **ラウンド制**: 全6ラウンド固定
2. **鬼の選出**: 各ラウンド開始時にランダムで鬼を選出
3. **TNT譲渡**: TNT保持者が非保持者を殴ることでTNTを押し付ける
4. **爆発**: カウントダウン終了時にTNT保持者のみ爆発（巻き込みなし）
5. **PVP無効**: プレイヤー同士のダメージは無効（TNT譲渡のみ可能）
6. **勝利条件**: 最後まで生き残ったプレイヤーが勝利

### ラウンド詳細
| ラウンド | TNT保持者数 | 爆発時間 | 特殊効果 |
|---------|------------|---------|---------|
| 1 | 1人 | 40秒 | なし |
| 2 | 全体の1/4 | 30秒 | なし |
| 3 | 全体の1/4 | 30秒 | なし |
| 4 | 全体の1/2 | 25秒 | なし |
| 5 | 全体の1/3 | 40秒 | 全員発光 |
| 6 | 全体の1/3 | 50秒 | 全員発光 |

### 移動速度設定
- **通常プレイヤー**: Speed I（移動速度上昇I）
- **TNT保持者（鬼）**: Speed II（移動速度上昇II）

### パラメータ設定
```yaml
game:
  min_players: 20             # 最小プレイヤー数
  max_players: 25             # 最大プレイヤー数
  rounds: 6                   # ラウンド数（固定）
  tag_cooldown: 0.5           # タグ後のクールダウン（秒）
  explosion_countdown: 3      # 爆発前のカウントダウン（秒）
  pvp_enabled: false          # PVPダメージ無効
  
round_settings:
  round_1:
    tnt_holders: 1            # TNT保持者数
    duration: 40              # 爆発までの時間（秒）
    glowing: false            # 発光効果
  round_2:
    tnt_holders_ratio: 0.25   # TNT保持者の割合（25%）
    duration: 30
    glowing: false
  round_3:
    tnt_holders_ratio: 0.25
    duration: 30
    glowing: false
  round_4:
    tnt_holders_ratio: 0.5    # TNT保持者の割合（50%）
    duration: 25
    glowing: false
  round_5:
    tnt_holders_ratio: 0.33   # TNT保持者の割合（33%）
    duration: 40
    glowing: true             # 全員発光
  round_6:
    tnt_holders_ratio: 0.33
    duration: 50
    glowing: true
    
arena:
  spawn_teleport: true        # ラウンド開始時に中央へテレポート
  use_preset_map: true        # 事前作成マップを使用
```

## ゲームメカニクス

### TNTシステム
- **視覚的表現**:
  - TNT保持者の頭がTNTブロックに変化
  - 赤い発光エフェクト（ラウンド5,6では全員発光）
  - パーティクルエフェクト（煙、火花）
  - 残り時間に応じてTNTの点滅速度が変化

- **音響効果**:
  - TNT保持時: 導火線の音（ループ）
  - タグ成功時: 衝撃音
  - 爆発5秒前: 警告音
  - 爆発時: 大爆発音

### タグメカニクス
- **タグ判定**: 
  - 近接攻撃（左クリック）でタグ
  - TNT保持者が非保持者を殴ることでTNT譲渡
  - 判定距離: 3ブロック
  - クールダウン: 0.5秒（連続タグ防止）

- **タグ成功時の処理**:
  1. TNTを相手に移動
  2. 元保持者のSpeed IIをSpeed Iに変更
  3. 新保持者にSpeed IIを付与
  4. エフェクトとサウンドを再生

### 爆発メカニクス
- **爆発処理**:
  - TNT保持者のみが爆発（巻き込みなし）
  - 爆発エフェクトは視覚的のみ
  - 爆発したプレイヤーは観戦モードへ移行
  - ブロック破壊なし

### アリーナシステム
- **事前作成マップ**: 管理者が事前に作成したマップを使用
- **マップ設定**:
  - 推奨サイズ: 50x50ブロック（20-25人用）
  - 中央スポーン地点の設定
  - 境界設定（ワールドボーダー）

- **環境要素**:
  - 障害物配置（固定）
  - 高低差のある地形
  - 隠れ場所の配置
  - 逃走ルートの確保

### ゲームフロー
1. **ゲーム開始前**:
   - ロビーで待機
   - 20人以上集まったらカウントダウン開始

2. **各ラウンド開始**:
   - 全員を中央にテレポート
   - TNT保持者をランダム選出
   - 3秒のカウントダウン後、移動可能

3. **ラウンド中**:
   - TNTの押し付け合い
   - 残り時間の表示
   - 爆発カウントダウン

4. **ラウンド終了**:
   - TNT保持者が爆発
   - 生存者を次ラウンドへ
   - 全ラウンド終了後リザルト表示

## UI/UX

### スコアボード表示
```
=== TNT TAG ===
ラウンド: 3/6
残り時間: 0:30

生存者: 15人
TNT保持者: 5人

あなたの状態: 生存中
```

### アクションバー
- 通常時: `残り時間: 45秒 | 生存者: 12人`
- TNT保持時: `⚠ TNTを持っています！他の人にタッチ！⚠`
- 爆発間近: `💥 爆発まで 3... 2... 1... 💥`

### タイトル表示
- ラウンド開始: `ROUND 3` / `TNTから逃げろ！`
- TNT取得時: `TNTを受け取った！` / `他のプレイヤーにタッチ！`
- 爆発時: `💥 BOOM! 💥` / `あなたは爆発しました`
- 勝利時: `🏆 VICTORY! 🏆` / `最後の生存者！`

### リザルト表示
```
========== GAME RESULT ==========
        🏆 最終結果 🏆

1位: PlayerName1 - 全ラウンド生存！
2位: PlayerName2 - ラウンド5まで生存
3位: PlayerName3 - ラウンド5まで生存
...

生存ラウンド数:
ラウンド6完走: 3人
ラウンド5脱落: 5人
ラウンド4脱落: 7人
ラウンド3脱落: 4人
ラウンド2脱落: 3人
ラウンド1脱落: 3人

総参加者: 25人
==================================
```

### サウンド設計
- BGM: 緊張感のあるループ音楽
- TNT保持: 心拍音の追加
- 残り10秒: テンポアップ
- 爆発前: ドラムロール

## 技術仕様

### イベント処理
```kotlin
// カスタムイベント
class TNTTagStartEvent(val game: TNTTagGame)
class TNTTagRoundStartEvent(val game: TNTTagGame, val round: Int)
class TNTTagEvent(val tagger: Player, val tagged: Player)
class TNTExplosionEvent(val victims: List<Player>)
class TNTTagEndEvent(val game: TNTTagGame, val winner: Player?)

// イベントリスナー
class TNTTagListener {
    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent)
    
    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEntityEvent)
    
    @EventHandler
    fun onPlayerDamage(event: EntityDamageByEntityEvent)
}
```

### データ構造
```kotlin
data class TNTTagGame(
    val id: String,
    val arena: Arena,
    val players: MutableList<Player>,
    val settings: GameSettings,
    var state: GameState,
    var currentRound: Int,
    val tntHolders: MutableSet<Player>,
    val eliminated: MutableSet<Player>,
    val scores: MutableMap<Player, Int>
)

enum class GameState {
    WAITING,
    STARTING,
    IN_GAME,
    ROUND_ENDING,
    ENDING
}
```

### パフォーマンス最適化
- **非同期処理**: 
  - スコア計算
  - データ保存
  - パーティクル計算

- **キャッシュ**:
  - プレイヤー位置（0.5秒更新）
  - 距離計算結果

- **リソース管理**:
  - パーティクル制限（視野内のみ）
  - エンティティ数制限

## 実装優先順位

### フェーズ1: コア機能
1. ゲームの開始・終了処理
2. TNTの割り当てと移動
3. タグシステムの実装
4. 爆発と脱落処理
5. 基本的なUI（スコアボード、タイトル）

### フェーズ2: ゲーム体験向上
1. エフェクトとサウンド
2. 複数アリーナ対応
3. 詳細な設定項目
4. 統計システム

### フェーズ3: 追加要素
1. パワーアップシステム
2. カスタムアリーナエディタ
3. リプレイシステム
4. トーナメントモード

## テスト項目

### 機能テスト
- [ ] 最小/最大人数でのゲーム開始
- [ ] TNT割り当ての正確性
- [ ] タグ判定の精度
- [ ] 爆発タイミングの正確性
- [ ] スコア計算の正確性
- [ ] 途中参加/退出の処理

### パフォーマンステスト
- [ ] 24人同時プレイでのTPS維持
- [ ] パーティクル大量表示時の負荷
- [ ] 長時間稼働時のメモリリーク確認

### エッジケース
- [ ] 同時タグの処理
- [ ] ラグ環境でのタグ判定
- [ ] アリーナ外への移動
- [ ] 全員がTNT保持者の場合

## 今後の拡張案

### ゲームモード追加
1. **Hot Potato Mode**: TNTが1つだけ、保持時間でダメージ
2. **Infection Mode**: TNT保持者が増えていく
3. **Team Mode**: チーム対抗戦
4. **Elimination Mode**: 毎ラウンド最下位が脱落

### カスタマイズ要素
- スキン: TNTの見た目変更
- トレイル: 移動時のパーティクル
- 爆発エフェクト: カスタム爆発演出
- 勝利ダンス: 勝利時のエモート

### ソーシャル機能
- フレンドシステム
- パーティ機能
- 観戦モード
- リプレイ共有

### 競技要素
- ランクシステム
- シーズン制
- リーダーボード
- 実績システム

## 設定ファイル例

### config.yml
```yaml
# TNT TAG Configuration
plugin:
  language: ja_JP
  debug: false
  
database:
  type: YAML  # YAML, SQLite, MySQL
  
game:
  lobby_world: world
  min_players: 4
  max_players: 24
  countdown: 10
  
rewards:
  enabled: true
  win: 100
  per_round_survive: 20
  per_tag: 5
```

### arenas.yml
```yaml
arenas:
  arena1:
    name: "Classic Arena"
    world: tnt_world
    type: MEDIUM
    center: 
      x: 0
      y: 64
      z: 0
    radius: 20
    spawn_points:
      - {x: 10, y: 64, z: 0}
      - {x: -10, y: 64, z: 0}
      # ...
```

### messages_ja_JP.yml
```yaml
game:
  start: "&a&lゲーム開始！TNTから逃げろ！"
  round_start: "&e&lラウンド %round% 開始！"
  tnt_received: "&c&lTNTを受け取った！他の人にタッチ！"
  tnt_passed: "&a&lTNTを渡した！安全だ！"
  explosion: "&4&l💥 ドカーン！💥"
  eliminated: "&cあなたは爆発しました..."
  victory: "&6&l🏆 勝利！最後の生存者です！"
  
errors:
  not_enough_players: "&cプレイヤーが不足しています（最小: %min%人）"
  arena_not_found: "&cアリーナが見つかりません"
  already_in_game: "&c既にゲームに参加しています"
```

## コマンド一覧

### プレイヤーコマンド
- `/tnttag join [arena]` - ゲームに参加
- `/tnttag leave` - ゲームから退出
- `/tnttag stats [player]` - 統計を表示
- `/tnttag list` - アリーナ一覧

### 管理者コマンド
- `/tnttag create <arena> <type>` - アリーナ作成
- `/tnttag delete <arena>` - アリーナ削除
- `/tnttag setspawn <arena>` - スポーン地点設定
- `/tnttag start <arena>` - ゲーム強制開始
- `/tnttag stop <arena>` - ゲーム強制終了
- `/tnttag reload` - 設定リロード

## 権限ノード
```yaml
tnttag.play              # ゲーム参加
tnttag.stats            # 統計閲覧
tnttag.admin            # 管理者コマンド
tnttag.vip              # VIP機能（優先参加）
tnttag.spectate         # 観戦モード
tnttag.bypass.cooldown  # クールダウン無視
```

## Active Technologies
- Java 17 (for Minecraft 1.20.x compatibility) (001-tnt-tag-game)
- YAML files (config.yml, arenas.yml, messages_ja_JP.yml, player stats) (001-tnt-tag-game)
- Java 21, Kotlin 2.2.21 (混在プロジェクト) + Paper API 1.21.10-R0.1-SNAPSHOT (Minecraft server platform), Kotlin stdlib-jdk8 (003-auto-game-start)
- YAML files (config.yml, arenas.yml, player stats) (003-auto-game-start)

## Recent Changes
- 001-tnt-tag-game: Added Java 17 (for Minecraft 1.20.x compatibility)
