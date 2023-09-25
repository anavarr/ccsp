from termcolor import colored
import os

dirs = os.listdir()

rules =\
{ 
    "SP_behaviours": "behaviour",
    "SP_programs": "program"
}


for dir in dirs:
    if not os.path.isfile(dir):
        w = os.walk(dir)
        if os.system(f"figlet {dir}"):
            print(f"===== {dir} =====")
        files = [f for f in os.listdir() if os.path.isfile(f)]
        for (dirpath, dirnames, files) in w:
            for f in files:
                ret = os.system(f"antlr4-parse ../../main/antlr4/SPparser.g4 ../../main/antlr4/SPlexer.g4 {rules[dir]} {dir}/{f} > out.txt")
                with open("out.txt", "r") as out:
                    o = out.read().replace("\n", "")
                    if o != "":
                        print(colored(f"{f} - There was an error : {o}", 'red'))
                        os.system(f"antlr4-parse ../../main/antlr4/SPparser.g4 ../../main/antlr4/SPlexer.g4 {rules[dir]} {dir}/{f} -gui")
                    else:
                        print(colored(f"{f} - Success", 'green'))