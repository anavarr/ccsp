client [
    If myConditionIsTrue()
    Then
        server!myRequest @!"";
        proxy!myData @!"";
        server?res @?"";
        proxy?myCorrectedData @?"";
        End
    Else
        server!myOtherRequest @!"";
        proxy!myOtherData @!"";
        server?res @?"";
        proxy?myCorrectedOtherData @?"";
        End
]