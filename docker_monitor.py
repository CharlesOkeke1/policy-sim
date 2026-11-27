
import subprocess
import json
import re
import time
import os

def to_bytes(value):
    match = re.match(r"([\d.]+)\s*([a-zA-Z]+)", value.strip())
    if not match:
        return 0

    number = float(match.group(1))
    unit = match.group(2)

    units = {
        "B": 1,
        "kB": 1000,
        "MB": 1000**2,
        "GB": 1000**3,
        "TB": 1000**4,
        "KiB": 1024,
        "MiB": 1024**2,
        "GiB": 1024**3,
        "TiB": 1024**4
    }

    return number * units.get(unit, 1)


def format_bytes(value):
    for unit in ["B", "KiB", "MiB", "GiB", "TiB"]:
        if value < 1024:
            return f"{value:.2f}{unit}"
        value /= 1024
    return f"{value:.2f}PiB"


def parse_pair(value):
    parts = value.split("/")
    return to_bytes(parts[0]), to_bytes(parts[1])


def get_stats():
    result = subprocess.run(
        ["docker", "stats", "--no-stream", "--format", "{{json .}}"],
        capture_output=True,
        text=True,
        check=True
    )

    return [json.loads(line) for line in result.stdout.splitlines() if line.strip()]


while True:
    try:
        stats = get_stats()

        total_cpu = 0.0
        total_mem = 0
        total_limit = 0
        total_net_rx = 0
        total_net_tx = 0
        total_block_read = 0
        total_block_write = 0
        total_pids = 0

        rows = []

        for s in stats:
            cpu = float(s["CPUPerc"].replace("%", ""))
            mem, limit = parse_pair(s["MemUsage"])
            net_rx, net_tx = parse_pair(s["NetIO"])
            block_read, block_write = parse_pair(s["BlockIO"])
            pids = int(s["PIDs"])

            total_cpu += cpu
            total_mem += mem
            total_limit += limit
            total_net_rx += net_rx
            total_net_tx += net_tx
            total_block_read += block_read
            total_block_write += block_write
            total_pids += pids

            rows.append([
                s["Name"],
                f"{cpu:.2f}%",
                f"{format_bytes(mem)} / {format_bytes(limit)}",
                s["MemPerc"],
                f"{format_bytes(net_rx)} / {format_bytes(net_tx)}",
                f"{format_bytes(block_read)} / {format_bytes(block_write)}",
                str(pids)
            ])

        os.system("cls" if os.name == "nt" else "clear")

        headers = ["CONTAINER", "CPU", "MEM USAGE / LIMIT",
                   "MEM %", "NET RX / TX", "BLOCK R / W", "PIDS"]

        rows.append([
            "TOTAL",
            f"{total_cpu:.2f}%",
            f"{format_bytes(total_mem)} / {format_bytes(total_limit)}",
            f"{total_mem / total_limit * 100:.2f}%" if total_limit else "N/A",
            f"{format_bytes(total_net_rx)} / {format_bytes(total_net_tx)}",
            f"{format_bytes(total_block_read)} / {format_bytes(total_block_write)}",
            str(total_pids)
        ])

        widths = [
            max(len(headers[i]), *(len(row[i]) for row in rows))
            for i in range(len(headers))
        ]

        print("POLICYSIM - DOCKER RESOURCE MONITOR")
        print(time.strftime("%Y-%m-%d %H:%M:%S"))
        print()

        print(" | ".join(headers[i].ljust(widths[i])
                         for i in range(len(headers))))
        print("-+-".join("-" * w for w in widths))

        for row in rows:
            print(" | ".join(row[i].ljust(widths[i])
                             for i in range(len(row))))

        print("\nPress Ctrl+C to stop.")

        time.sleep(1)

    except KeyboardInterrupt:
        print("\nMonitor stopped.")
        break
    except Exception as e:
        print(f"Error: {e}")
        time.sleep(2)
