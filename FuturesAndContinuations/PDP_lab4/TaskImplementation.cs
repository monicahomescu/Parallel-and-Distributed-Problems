using System.Net.Sockets;
using System.Text;

namespace PDP_lab4
{
    public class TaskImplementation
    {
        public static void Run(string[] hosts)
        {
            var tasks = new Task[hosts.Length];

            for (var i = 0; i < hosts.Length; i++)
            {
                var id = i;
                tasks[i] = Task.Factory.StartNew(() => StartDownload(id, hosts[id]));
            }

            Task.WaitAll(tasks);
        }

        private static void StartDownload(int id, string host)
        {
            //configure the connection information
            var state = Utils.GetState(id, host);

            //call functions
            Connect(state).Wait();
            Send(state).Wait();
            Receive(state).Wait();

            //print the response
            Console.WriteLine(state.response.ToString());

            //close the connection
            state.socket.Shutdown(SocketShutdown.Both);
            state.socket.Close();
        }

        private static Task Connect(State state)
        {
            //begin a request for the remote host connection
            //remoteEP (EndPoint) - an EndPoint that represents the remote host
            //callback (AsyncCallback) - the method to call when the operation is complete
            //state (Object) - an object that contains state information for the request
            state.socket.BeginConnect(state.remoteEndPoint, ConnectCallback, state);

            //wait for the asynchronous connect operation to complete
            return Task.FromResult(state.connectEvent.WaitOne());
        }

        private static void ConnectCallback(IAsyncResult asyncResult)
        {
            //get the connection information
            var state = (State)asyncResult.AsyncState;

            //end the pending connection request
            state.socket.EndConnect(asyncResult);

            //print the status
            Console.WriteLine("(" + state.id + ") connected to " + state.host);

            //signal that the connection has been made
            state.connectEvent.Set();
        }

        private static Task Send(State state)
        {
            //convert the get request string to bytes and add to buffer
            var buffer = Encoding.ASCII.GetBytes(Utils.GetRequest(state.host, state.endPointPath));

            //send data to the connected socket
            //buffer (Byte[]) - the buffer that contains the data to send
            //offset (Int32) - the position in the buffer at which to begin sending data
            //size (Int32) - the number of bytes to send
            //socketFalgs (SocketFlags) - a bitwise combination of the SocketFlags values
            //callback (AsyncCallback) - the method to call when the operation is complete
            //state (Object) - an object that contains state information for the send operation
            state.socket.BeginSend(buffer, 0, buffer.Length, 0, SendCallback, state);

            //wait for the asynchronous send operation to complete
            return Task.FromResult(state.sendEvent.WaitOne());
        }

        private static void SendCallback(IAsyncResult asyncResult)
        {
            //get the connection information
            var state = (State)asyncResult.AsyncState;

            //end the pending send started by BeginSend
            //asyncResult (IAsyncResult) - the state information created by the matching BeginSend call
            var numberOfBytesSent = state.socket.EndSend(asyncResult);

            //print the status
            Console.WriteLine("(" + state.id + ") sent " + numberOfBytesSent + " bytes");

            //signal that all the bytes have been sent
            state.sendEvent.Set();
        }

        private static Task Receive(State state)
        {
            //begin to receive data from the connected socket
            //buffer (Byte[]) - the storage location for the received data
            //offset (Int32) - the location in buffer to store the received data
            //size (Int32) - the number of bytes to receive
            //socketFlags (SocketFlags) - a bitwise combination of the SocketFlags values
            //callback (AsyncCallback) - the method to call when the operation is complete
            //state (Object) - a user-defined object that contains information about the receive operation
            state.socket.BeginReceive(state.buffer, 0, State.size, 0, ReceiveCallback, state);


            //wait for the asynchronous receive operation to complete
            return Task.FromResult(state.receiveEvent.WaitOne());
        }

        private static void ReceiveCallback(IAsyncResult asyncResult)
        {
            //get the connection information
            var state = (State)asyncResult.AsyncState;

            //end the pending read started by BeginReceive
            //asyncResult (IAsyncResult) - the state information created by the matching BeginRecieve call
            var numberOfBytesReceived = state.socket.EndReceive(asyncResult);

            //convert the bytes from the buffer to string and append to response
            state.response.Append(Encoding.ASCII.GetString(state.buffer, 0, numberOfBytesReceived));

            //check the response header
            if (!Utils.CheckResponseHeader(state.response.ToString()))
            {
                //the response header is not complete

                //get the next part of the header
                state.socket.BeginReceive(state.buffer, 0, State.size, 0, ReceiveCallback, state);
            }
            else
            {
                //the response header is complete

                //get the content length from the header line
                var contentLength = Utils.GetContentLength(state.response.ToString());

                //get the body of the response
                var responseBody = Utils.GetResponseBody(state.response.ToString());

                //compare the length of the response body to the content length from the header line
                if (responseBody.Length < contentLength)
                {
                    //the response body is not complete

                    //get the next part of the response body
                    state.socket.BeginReceive(state.buffer, 0, State.size, 0, ReceiveCallback, state);
                }
                else
                {
                    //the response body is complete

                    //signal that all the bytes have been received
                    state.receiveEvent.Set();
                }
            }
        }
    }
}
