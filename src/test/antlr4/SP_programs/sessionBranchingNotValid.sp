server [
    If myCondition
    Then
        proxy!myData @!"";
        proxy?myData @?"";
        End
    Else
        server!myData @!"";
        server?myData @?"";
        End
]