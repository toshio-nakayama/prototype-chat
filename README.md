<h1 align="center">ポートフォリオ紹介</h1>

## アプリケーション名

  「PrototypeChat」

## アプリケーション概要

   リアルタイムなメッセージのやり取りを実現する１対１コミュニケーションアプリです。
   主要な機能には、Firebase Authenticationを活用したユーザーの認証、QRコードリーダーを活用した
   チャット相手の追加、チャットルームにおいてのメッセージの送受信などがあります。
   レイアウトはシンプルで見やすくなるよう心がけて制作しました。

## 開発環境

* ビルドツール: Gradle7.4
* 実機OS: Android OS12
* minSdk: 29
* TargetSdk: 33
* jvmTarget: 11
* 開発言語: Kotlin
* エディタ:Android Studio Dolphin | 2021.3.1 Patch 1
* OS: macOS13.1

## 開発の目的

   アプリケーションエンジニアになるため、全般的な開発スキルを習得する目的で制作しました。
   チャットアプリの制作にはデータベース設計やリアルタイムに画面を更新する仕組みなど、工夫すべき
   点が多数あり多くを学べると考えました。開発言語にKotlinを使用し、モダンな言語に対応できる技術
   を身につけること、一から設計から開発までを経験することにより、課題解決スキルとプログラム開発
   の経験を積むことを目標として取り組みました。

## 工夫したこと

   Firebaseからのデータの取得にはLiveDataの仕組みを使って責務を分散させ、
   メッセージ送信時間の加工にはZonedDateTimeやDateTimeFormatterなどを使って表示のバリエーションを増やしました。
   使うツールや言語のドキュメントを熟読し、多様な表現力を身につけるように心がけました。

## 苦労したこと

   メッセージ送信後のタイミングで最新のメッセージを表示するページに切り替えるなど、メッセージリストを適切な位置で表示するために試行錯誤しました。
   変更すべき箇所を洗い出し、テストを繰り返した結果、RecyclerViewのスクロール位置を受信と更新のタイミングで変更する解決策を導き出しました。
   この経験により、詳細にViewを操作するスキルが身につきました。

## 機能紹介

---

### ユーザー認証

   <p>
   <img src="https://user-images.githubusercontent.com/99377294/225788202-3512d197-a89f-44fa-85ed-82340b160df6.png" width="240" align="left" hspace="5">
   ユーザーの登録・認証にはFirebase Authenticationを使い、パスワードとメールアドレスでユーザーを管理します。
   <br>
   ログイン実行時にはインターネット接続チェックを行い、確実にログインできるようにしています。
   <br>
   各入力項目にはフォームバリデーションを追加し、データの整合性を保てるように工夫しています。
   <br>
   </p>
   <br clear="all"></br>

   ---

### プロフィール表示

   <p>
   <img src="https://user-images.githubusercontent.com/99377294/225788337-73957515-ce99-429a-ab72-567de658d3a5.png" width="240" align="left" hspace="5">
   ユーザー詳細情報を管理するコレクションをFirebaseに作成し、データの取得にはValueEventListenerを使用しました。
   <br>
   Fragment生成時にこれらの機能を使いデータを取得します。
   <br>
   また、この画面から、プロフィール編集機能へとアクセスできます。
   <br>
   </p>
   <br clear="all"></br>

   ---

### プロフィール編集

   <p>
   <img src="https://user-images.githubusercontent.com/99377294/225788389-ddf77e9b-9de4-4aa6-919e-74eecc449144.png" width="240" align="left" hspace="5">
   ActivityResultContractなどの機能を使用して、画像ライブラリにアクセスし、選択した画像をアプリ内で使用できるようにしました。
   <br>
   取得した画像は直ちにFirebaseStorageに保存され、そのタイミングでリスナーがデータを取得し、リアルタイムに画面が更新されます。
   <br>
   </p>
   <br clear="all"></br>

   ---

### テキスト編集

   <p>
   <img src="https://user-images.githubusercontent.com/99377294/225788414-1bf60b28-853e-45be-8bd8-e0d3925a8a1d.png" width="240" align="left" hspace="5">
   テキスト編集専用の機能をダイアログフラグメントで実現しました。文字数カウンタやバリデーション機能を備えています。
   <br>
   再利用できるように、設定したい項目をパラメータとして渡し、制限文字数を変更できるように工夫しました。
   <br>
   </p>
   <br clear="all"></br>

   ---

### QRコード読み取り

   <p>
   <img src="https://user-images.githubusercontent.com/99377294/225788443-7d74c927-5917-4bf1-86c7-feaa07a57900.png" width="240" align="left" hspace="5">
   Zxingライブラリを使ってQRコード読み取り機能を実現しました。独自のレイアウトを採用し、視認性を向上させています。
   <br>
   読み取りに成功した場合は、即座にデータを返し、すでに登録されていたり、不正なデータが読み込まれた場合は、トーストメッセージでユーザーに通知します。
   <br>
   </p>
   <br clear="all"></br>

   ---

### QRコード生成

   <p>
   <img src="https://user-images.githubusercontent.com/99377294/225788463-4f7d469b-1dc1-48a4-bade-4c9bfc69779d.png" width="240" align="left" hspace="5">
   FirebaseAuthenticationで作成された一意のuidをQRコード生成に使用しました。
   <br>
   String型のuidをZxingのMultiFormatWriterで400×400ピクセルのビット配列に変換し、ImageViewに表示しています。
   <br>
   QRコード読み取り機能と連携して、ユーザー情報のQRCodeGeneratorFragment.kt 交換が可能です。
   <br>
   </p>
   <br clear="all"></br>

   ---

### トーク履歴

   <p>
   <img src="https://user-images.githubusercontent.com/99377294/225788488-5a685524-3147-4729-8406-f02a9d46e4ea.png" width="240" align="left" hspace="5">
   リスト表示にはListAdapterとDiffUtil.ItemCallbackを使い、差分のみを更新させることで、UI更新にかかるコストを抑えました。
   <br>
   ShimmerFrameLayoutを仕様してロード時に自然なエフェクトがかかるようにしています。
   <br>
   リストにはトーク送信時に含まれる最終送信メッセージ、最終送信時間、メッセージ未読数を表示します。
   <br>
   最終送信時間は現在の時刻と比較し、細かくフォーマットを変更するように工夫しました。
   <br>
   </p>
   <br clear="all"></br>

   ---

### メッセージ送受信

   <p>
   <img src="https://user-images.githubusercontent.com/99377294/225788516-a9cdab8d-9221-459b-bc9d-178be71fa162.png" width="240" align="left" hspace="5">
   １行分のレイアウトには双方のメッセージを１段ずらして左右に配置しました。
   <br>
   ListAdapterに変更が通知された際のデータに含まれる送信者情報でレイアウトの可視状態を切り替えることで自然なレイアウトを実現しました。
   <br>
   スワイプにより、最新の状態に更新することができます。
   <br>
   </p>
   <br clear="all"></br>

---
