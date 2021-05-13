import time

def main():
	while True:
	    with open("/usr/src/app/hellotext/hello.txt") as f:
	        print(f.read(), flush=True)
	        time.sleep(5)


if __name__ == "__main__":
	main()
