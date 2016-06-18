# IRKitService
IRKit ( http://getirkit.com/ ) をWebサービスでラップし，Web-APIを持たせる

## 環境
- Java8
- Jersey2系
- Tomcat7
- MongoDB3.2.1

## IRKit仕組み
- APIは少ない

### GET /messages
- 最も新しい受信した赤外線信号を返します。
- 受信は自動，というかいつでもIRKitに向かってリモコンで赤外線を送ると保存してくれるっぽい

### POST /messages
- 赤外線信号を送ります。
- GETで返ってきたJSONをそのまま送る感じ

### POST /keys
- clienttoken を取得します。 clienttoken を次に IRKit Internet HTTP API の POST /1/keys へのリクエストにのせることで clientkey, deviceid を取得することができます。 詳しくは IRKit Internet HTTP API 参照

### POST /wifi
- IRKitは家のWiFiアクセスポイントに接続して動作しますが、 そのためには、IRKitは家のWiFiアクセスポイントのセキュリティ(WPA2/WEP/NONE)、SSID、パスワードを知る必要があります。
- アプリから設定できない場合とか

## IRKitService API仕様
### IR保存形式 @ mongoDB
{“ir_id”:”hogehoge”, “ir_info”:{"format":"raw","freq":38,"data":[17421,8755,1150,1150,1150,1150,1150,1150,1150,1150,1150,1150,1150,1150,1150,3228,1150,1150,1150,3228,1150,3228,1150,3228,1150,3228,1150,3228,1150,3228,1150,1150,1150,3228,1150,1150,1150,1150,1150,3228,1150,3228,1150,3228,1150,1150,1150,1150,1150,1150,1150,3228,1150,3228,1150,1150,1150,1150,1150,1150,1150,3228,1150,3228,1150,3228,1150,65535,0,13693,17421,4400,1150]
}}

### String LearnIR(String id)
- IR信号を学習する
- 返り値はIRKit API のGET /messagesの結果JSON
- 予め，/messagesを実行しておいて，保存されているIR情報が無いようにする
- そこから，受信待ちにしておいて，数秒ごとに/messagesをポーリング
    - 返り値があった場合ループを抜けて，DBに保存して結果をreturn
        - 30秒待っても信号がなければ何もせずreturn
        - 命名規則
            - [家電名]_[操作名]
            - tv_pow, light_on など

### boolean sendIR(String id)
- IR信号を送信する
    - 返り値はboolean 
        -成功したらtrue 指定したidがDBに存在しなければfalse
        - idをキーにしてDBを探す

### String list()
- DBに登録されているIR信号のリストを返す
