import subprocess
import time


def run_adb_and_capture_logs(package_name, duration_s, expected_crash_message, silent):
    # Launch the app
    subprocess.run(
        [
            "adb",
            "shell",
            "monkey",
            "-p",
            package_name,
            "-c",
            "android.intent.category.LAUNCHER",
            "1",
        ],
        stdout=subprocess.DEVNULL if silent else None,
        stderr=subprocess.DEVNULL if silent else None,
    )

    # Start logcat
    logcat_process = subprocess.Popen(
        ["adb", "logcat"],
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True,
    )

    # Read logs for the given duration
    crash_found = False
    start_time = time.time()
    while time.time() - start_time < duration_s:
        line = logcat_process.stdout.readline()
        if not silent:
            print(line)
        if expected_crash_message in line:
            print("Crash message found!")
            crash_found = True
            break

    # Stop logcat process
    logcat_process.terminate()
    # Stop app
    subprocess.run(
        ["adb", "shell", "am", "force-stop", package_name],
        stdout=subprocess.DEVNULL if silent else None,
        stderr=subprocess.DEVNULL if silent else None,
    )

    return crash_found


if __name__ == "__main__":
    max_runs = 100
    crash_found = False
    # Run the app multiple times
    for i in range(max_runs):
        print(f"Run {i + 1}/{max_runs}...")
        crash_found = run_adb_and_capture_logs(
            package_name="com.meta.levinriegner.mediaview.qa",
            duration_s=4,
            expected_crash_message="No panel creator found for key",
            silent=True,
        )
        if crash_found:
            break
    print("Crash found!" if crash_found else "No crash found!")
