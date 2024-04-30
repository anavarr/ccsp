proc [
    If check(x)
    Then
        client?data @?"";
        Call Y
    Else
        Call Y
]

Y:
    client!query @!"";
    Call Y


