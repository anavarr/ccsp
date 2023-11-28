server [
    If myCondition
    Then
        proxy!myData @!"";
        proxy?myData @?"";
        End
    Else
        otherServer!myData @!"";
        otherServer?myData @?"";
        End
]