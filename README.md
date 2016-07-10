# ShogiBoardSearch
将棋の局面検索を行うプログラムです。
一致局面検索と類似局面検索に対応しており、Javaで実装されています。

## 制限
- 平手棋譜のみ対応しています。 (※)
- 分岐のない棋譜のみ対応しています。 (※)
- 文字コードがShift-JISのkifファイル(拡張子がkif)のみ対応しています。
- 全棋譜データのインデックスが一時的にメモリ上にのるため、大量データのインデックスを生成する際はご注意ください。

(※)明示的に除外する実装を入れているわけではなく、分岐のない平手棋譜として処理中にエラーが発生したらインデックス生成の対象から除外しています。

## 使い方
1. 右にある緑色の「Clone or download」、「Download ZIP」の順にクリックしてzipをダウンロードします。
2. zipを解凍し、outフォルダにあるShogiBoardSearchフォルダを好きなところに配置します。
3. KifuDataIndexer.batをテキストエディタで開き、[KIFU_FILE_PATH]を棋譜ファイルのあるフォルダパスに置き換えます。フォルダパス配下にある棋譜ファイルが全てインデックス生成の対象になります。
4. KifuDataIndexer.batを実行します。
5. 局面転置インデックスがindexフォルダ配下に生成され、インデックスに取り込まれなかった棋譜ファイルのパスがlog\kifuDataIndexerFailure.logに出力されます。
6. Kifu for Windowsを開き、[ツール] > [ツールメニュー設定]を開きます。
![ツールメニュー設定](http://members.petanko.org/wp/ottfoekst/files/2016/07/ShogiBoardSearch_readme1.png)
7. 一致局面検索ツール、類似局面検索ツールをそれぞれ下記の画像のように設定します。類似局面検索の最後の引数70は、検索結果に表示する類似度の最小値で、0以上100以下の値を設定可能です。
![一致局面検索ツールの設定](http://members.petanko.org/wp/ottfoekst/files/2016/07/ShogiBoardSearch_readme2.png)
![類似局面検索ツールの設定](http://members.petanko.org/wp/ottfoekst/files/2016/07/ShogiBoardSearch_readme3.png)
8. 任意の棋譜ファイルを開き、ツールメニューかショートカットキー(Ctrl + 数字)で検索を実行します。
9. ツールを実行したときの局面で一致局面検索/類似局面検索した結果がlog\result.logに出力されます。

## 注意
- 本プログラムの使用は自己責任でお願いします。
- 不具合や要望等があればご連絡ください。ただし修正/機能拡張を行うことを保証するものではありません。
