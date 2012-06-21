Licensed under the Apache License, Version 2.0 


[概要]  
これは株式会社コニットが提供するSamurai Purchaseを利用したサンプルアプリです。  
ライセンスはApache License2.0です。  
コードの改変、再配布は自由ですが、その上でSamurai Purchaseの機能を利用するには一定の手順を踏む必要があります。  
詳しくは下記[コードの改変について]を御覧ください。  

[コードの改変について]  
1.Samurai Purchaseを利用する前提のアプリとなります。Samurai Purchaseに申し込みをしてください。( http://www.conit.jp/ )  
2.Samurai Purchaseにアプリケーションを登録してください。登録時にアクセストークンとAPIサーバーホスト名が割り振られます。  
　アクセストークンはSamurai PurchaseAPIを呼び出すのに使用します。  
　APIサーバーホスト名は、/src/jp/co/conit/sss/sp/ex1/util/SSSApiUtil.java の48行目のDOMAINの値を割り振られたAPIサーバーホスト名に書き換えて下さい。  
3.ドキュメント( http://sss-developer.conit.jp/samurai_purchase/usage/about_token_method.html )を参考にndk（libtoken.so）をダウンロードし、libs/armeabi配下の同名ファイルと置き換えます。  
　3-1.パッケージ＋クラス名の確認  
　　当アプリでは「jp.co.conit.sss.sp.ex1.util.Util」となっています。  
　　改変時にパッケージ名などを変更している場合は注意してください。  
　3-2.PackageManagerのsignatureをsha1ハッシュ化したものの値を確認  
　　当アプリではフラグを切り替えることでsignatureを取得、表示できるようになっています。  
　　　 1. /src/jp/co/conit/sss/sp/ex1/activity/MainActivity.javaのSHOW_SIGUNATUREの値をtrueに変更します。  
　　　 2.署名したapkを作成します。  
　　　 3.アプリを起動するとダイアログ上に値が表示されます。Logへ出力もしています。（Tag:MainActivity）  
　　　　　※確認後はSHOW_SIGUNATURE をfalseに戻して下さい。  
　　　　　※SHOW_SIGUNATUREがtrueの場合、アプリは上記値を表示する機能のみを提供します。  
  3-3.ndk（libtoken.so）をダウンロード  
　　トークン確認画面でndk Downloadのボタンを押します。  
　　　パッケージ＋クラス名：3-1.で確認したパッケージ＋クラス名を入力  
　　　利用キー：3-2.で確認したsignatureをsha1ハッシュ化したものの値を入力  
　　実行ボタンを押すとndk（libtoken.so）がダウンロードされます。  
  3-4.ndk（libtoken.so）の差し替え  
　　libs/armeabi配下の同名ファイルと置き換えます。  
4.画面左のメニューよりSamurai Purchase＞プロダクト一覧と選択して、「新規プロダクトを登録する」からコンテンツの情報を登録して下さい。  
　※無料コンテンツを登録したい場合には、必ず「無料プロダクト」にチェックを入れて下さい。この項目は後から変更することができないためです。  
5.コンテンツ情報の登録後、「ファイル情報の更新」よりコンテンツの本体となるhtmlファイルを登録して下さい。  
　書籍ビューワーはhtmlファイルをWebViewで読み込み、表示しています。そのためビューワーを表示させるためにはhtmlファイルを登録する必要があります。  
　※このサンプルアプリは1プロダクトに関連付けされるファイルは1つのみを想定した作りとなっています。  
　　Samurai Purchaseのパッケージ機能やメタ情報機能を利用したい場合には別途アプリ側で対応が必要となりますので、  
　　開発者ドキュメント( http://sss-developer.conit.jp/index.html )のWebAPI＞リクエスト例などを参考に対応してください。  
6.コンテンツおよびその情報の登録が完了したら、各コンテンツにチェックを入れて「公開/非公開」のプルダウンリストから「選択したものを公開する」を選択、実行して下さい。  
  
ここまでの手順で、Samurai Purchaseに登録したプロダクト一覧が取得できるようになります。  
Samurai PurchaseとGoogle Play ストアと連携し、課金処理を確認するには以下の手順が必要となります。  
  
7.Android Developer Consoleからアプリケーションのアップロードを行なって下さい。  
8.Android Developer Consoleからアプリ内サービスを追加してください。  
　その際、Android Developer Consoleの「アプリ内サービスID」にSamurai Purchaseに登録されている「プロダクトID」と同一のIDを登録して、互いの紐付けを行なって下さい。  
  
注）動作確認をする際に、Android Developer Consoleでテストアカウントの追加を忘れないようにして下さい。  
  
  
[独自署名のみを行う場合について]  
[コードの改変について]3のドキュメンの通り、トークンの利用の際にPackageManagerで得られるSignature（署名によって異なる）を使用しています。  
また登録されているプロダクトリストはアクセストークンと紐付いています。  
そのため、独自に署名を行う場合のみでも[コードの改変について]の手順を踏む必要があります。  
アプリの動作確認のみを行いたい場合は[インストール方法]を参照しインストールを行なって下さい。  
  
[インストール方法]  
Google Play ストアからインストールを行なって下さい。  
https://play.google.com/store/apps/details?id=jp.co.conit.sss.sp.ex1  
  
[使用上の注意事項]  
・このアプリはサンプルアプリではありますが、アプリ内課金機能を実装しており、アプリから購入を行うとGoogle Play ストアからの請求が発生します。  
　これによって生じたいかなる不利益に対しても保証は出来かねますので、あらかじめご了承下さい。  
・アプリは十分にテストをしていますが、お使いの端末の環境や、プログラムの不具合などによって問題が生じる可能性があります。  
　これによって生じた、いかなる損害に対しても保証は出来かねますので、あらかじめご了承ください。  
  
[関連リンク]  
・Samuri Smartphone Services  http://www.conit.jp/  
・開発者ドキュメント  http://sss-developer.conit.jp/index.html#  
・株式会社コニット  http://www.conit.co.jp/  
・Blog  http://www.conit.co.jp/conitlabs/  
・Facebook  https://www.facebook.com/conit.fan  
・Twitter  https://twitter.com/#!/conit  
_  