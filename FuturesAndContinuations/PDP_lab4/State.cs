using System.Net.Sockets;
using System.Net;
using System.Text;

namespace PDP_lab4
{
    public class State
    {
        public int id;
        public string host;
        public Socket socket;
        public string endPointPath;
        public IPEndPoint remoteEndPoint;

        public const int size = 1024;
        public byte[] buffer = new byte[size];
        public StringBuilder response = new StringBuilder();

        public ManualResetEvent connectEvent = new ManualResetEvent(false);
        public ManualResetEvent sendEvent = new ManualResetEvent(false);
        public ManualResetEvent receiveEvent = new ManualResetEvent(false);
    }
}
