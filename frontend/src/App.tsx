// src/App.tsx
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context';
import { Navbar } from './components/layout';
import { Login, Profile, Signup } from './pages';
import { ProblemList } from './pages/ProblemList/ProblemList';
import { Workspace } from './pages/Workspace/Workspace';
import HomePage from './pages/Home/Home';
import { ContestList } from './pages/Contest/ContestList';

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="min-h-screen bg-dark-bg flex flex-col">
          <Navbar />
          <main className="flex-1 overflow-auto">
            <Routes>
              <Route path="/login" element={<Login />} />
              <Route path="/signup" element={<Signup />} />
              <Route path="/problems" element={<ProblemList />} />
              <Route path="/contest" element={<ContestList />} />
              <Route path="/problems/:problemId" element={<Workspace />} />
              <Route path="/profile" element={<Profile />} />
              <Route path="/profile/:userId" element={<Profile />} />
              <Route path='/' element={<HomePage />}></Route>
              <Route path='/home' element={<HomePage />}></Route>
            </Routes>
          </main>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;