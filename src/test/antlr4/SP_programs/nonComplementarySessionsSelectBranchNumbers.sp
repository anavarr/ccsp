client [
    If checkInput()
    Then
        server+"doThis" @+"";
        End
    Else
        server+"doThat" @+ "";
        End
]
|
server [
    client&
    { "doThis": None }
    //
    { "doThat": None }
    //
    { "doThisAndThat": None }
]